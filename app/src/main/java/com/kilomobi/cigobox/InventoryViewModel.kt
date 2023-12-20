package com.kilomobi.cigobox

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException

class InventoryViewModel : ViewModel() {
    private var restInterface: InventoryApiService
    private var inventoryDao = InventoryDb.getDaoInstance(CigoBoxApplication.getAppContext())
    val items = mutableStateOf(emptyList<Appetizer>())
    val allowEdit = mutableStateOf(false)
    val selectedFilter = mutableStateOf(Category.TOUT)

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("redacted")
            .build()
        restInterface = retrofit.create(InventoryApiService::class.java)
        getInventory()
    }

    private fun getInventory() {
        viewModelScope.launch(errorHandler) {
            val remoteList = getAllInventory()
            val appetizerList = remoteList.map { it.copy(isVisible = true) }
            items.value = appetizerList
        }
    }

    private suspend fun getAllInventory(): List<Appetizer> {
        return withContext(Dispatchers.IO) {
            try {
                refreshCache()
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException,
                    is ConnectException,
                    is HttpException -> {
                        if (inventoryDao.getAll().isEmpty())
                            throw Exception("Something went wrong. We have no data.")
                    }
                    else -> throw e
                }
            }
            return@withContext inventoryDao.getAll()
        }
    }

    private suspend fun refreshCache() {
        val remoteInventory = restInterface.getInventory()
        inventoryDao.addAll(remoteInventory)
    }

    private suspend fun updateRemoteQuantity(id: Int, quantity: Int): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                restInterface.updateQuantity(id, quantity)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun updateQuantity(id: Int, quantityChange: Int) {
        val appetizers = items.value.toMutableList()
        val itemIndex = appetizers.indexOfFirst { it.id == id }

        if (itemIndex != -1) {
            val item = appetizers[itemIndex]
            appetizers[itemIndex] =
                item.copy(
                    quantity = (item.quantity + quantityChange).coerceAtLeast(0),
                    isQuantityUpdated = true
                )
            items.value = appetizers
            viewModelScope.launch {
                updateQuantityAppetizer(id, item.quantity)
            }
        }
    }

    private suspend fun updateQuantityAppetizer(id: Int, value: Int) {
        withContext(Dispatchers.IO) {
            inventoryDao.update(PartialAppetizer(id, value))
            // Retrieve the content of our local db
            inventoryDao.getAll()
        }
    }

    fun increaseQuantity(id: Int, quantity: Int = 1) {
        updateQuantity(id, quantity)
    }

    fun decreaseQuantity(id: Int, quantity: Int = -1) {
        updateQuantity(id, quantity)
    }

    // Handle a bunch of items to subtract within a box
    fun subtractBox(operations: List<BoxOperation>) {
        operations.forEach {
            updateQuantity(it.id, it.quantity)
        }
    }

    fun toggleEdit() {
        allowEdit.value = !allowEdit.value
        toggleEditItems()
    }

    private fun toggleEditItems() {
        val currentList = items.value
        items.value = currentList.map { it.copy(isEditable = !it.isEditable) }
    }

    // Used to filters between categories, and return a list with correct items visibility
    fun filterAction(category: Category) {
        val currentList = items.value
        val filteredList =
            currentList.filter { it.category.toCategory().name == category.name || it.category == Category.TOUT.name }
        val visibleList = currentList.map { item ->
            item.copy(isVisible = item in filteredList)
        }
        selectedFilter.value = category
        items.value = visibleList
    }

    // Update each modified item on remote db and handle toggles
    fun validateStock() {
        viewModelScope.launch(errorHandler) {
            pushUpdatedQuantities(items.value.filter { it.isQuantityUpdated })
        }

        // Disable the editable view
        toggleEdit()
    }

    private suspend fun pushUpdatedQuantities(modifiedValues: List<Appetizer>) {
        return withContext(Dispatchers.IO) {
            modifiedValues.forEach {
                updateRemoteQuantity(it.id, it.quantity)
                updateQuantityAppetizer(it.id, it.quantity)
            }
            val newList = items.value.map { item ->
                item.copy(isQuantityUpdated = false)
            }
            items.value = newList
        }
    }
}