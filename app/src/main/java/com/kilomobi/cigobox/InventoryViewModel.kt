package com.kilomobi.cigobox

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InventoryViewModel : ViewModel() {
    private var restInterface: InventoryApiService
    val state = mutableStateOf(emptyList<Appetizer>())
    val allowEdit = mutableStateOf(false)

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
            val remoteList = getRemoteInventory()
            val appetizerList = remoteList.map { it.copy(isVisible = true) }
            state.value = appetizerList
        }
    }

    private suspend fun getRemoteInventory(): List<Appetizer> {
        return withContext(Dispatchers.IO) {
            restInterface.getInventory()
        }
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
        val appetizers = state.value.toMutableList()
        val itemIndex = appetizers.indexOfFirst { it.id == id }

        if (itemIndex != -1) {
            val item = appetizers[itemIndex]
            appetizers[itemIndex] =
                item.copy(quantity = (item.quantity + quantityChange).coerceAtLeast(0), isQuantityUpdated = true)
            state.value = appetizers
        }
    }

    fun increaseQuantity(id: Int) {
        updateQuantity(id, 1)
    }

    fun decreaseQuantity(id: Int) {
        updateQuantity(id, -1)
    }

    fun toggleEdit() {
        allowEdit.value = !allowEdit.value
        toggleEditItems()
    }

    private fun toggleEditItems() {
        val currentList = state.value
        state.value = currentList.map { it.copy(isEditable = !it.isEditable) }
    }

    // Used to filters between categories, and return a list with correct items visibility
    fun filterAction(category: Category) {
        val currentList = state.value
        val filteredList =
            currentList.filter { it.category.toCategory().name == category.name || it.category == Category.TOUT.name }
        val visibleList = currentList.map { item ->
            item.copy(isVisible = item in filteredList)
        }
        state.value = visibleList
    }

    // Update each modified item on remote db and handle toggles
    fun validateStock() {
        viewModelScope.launch(errorHandler) {
            pushUpdatedQuantities()
        }

        // Disable the editable view
        toggleEdit()
    }

    private suspend fun pushUpdatedQuantities() {
        return withContext(Dispatchers.IO) {
            val modifiedValues = state.value.filter { it.isQuantityUpdated }
            modifiedValues.forEach {
                updateRemoteQuantity(it.id, it.quantity)
            }
            val newList = state.value.map { item ->
                item.copy(isQuantityUpdated = true)
            }
            state.value = newList
        }
    }
}