package com.kilomobi.cigobox

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InventoryViewModel(private val stateHandle: SavedStateHandle) : ViewModel() {
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
            state.value = appetizerList.restoreSelections()
        }
    }

    private suspend fun getRemoteInventory(): List<Appetizer> {
        return withContext(Dispatchers.IO) {
            restInterface.getInventory()
        }
    }

    private fun updateQuantity(id: Int, quantityChange: Int) {
        val appetizers = state.value.toMutableList()
        val itemIndex = appetizers.indexOfFirst { it.id == id }

        if (itemIndex != -1) {
            val item = appetizers[itemIndex]
            appetizers[itemIndex] =
                item.copy(quantity = (item.quantity + quantityChange).coerceAtLeast(0))
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

    fun filterAction(category: Category) {
        val currentList = state.value
        val filteredList =
            currentList.filter { it.category.toCategory().name == category.name || it.category == Category.TOUT.name }
        state.value = currentList.map { item ->
            item.copy(isVisible = item in filteredList)
        }
    }

    fun validateStock() {
        toggleEdit()
    }

    private fun storeSelection(item: Appetizer) {
        val savedItems = stateHandle.get<List<Appetizer>?>(APPETIZERS)
            .orEmpty().toMutableList()
        savedItems.clear()
        savedItems.add(item)
        stateHandle[APPETIZERS] = savedItems
    }

    private fun List<Appetizer>.restoreSelections(): List<Appetizer> {
        stateHandle.get<List<Appetizer>?>(APPETIZERS)?.let {
            val restaurantsMap = this.associateBy { it.id }
            return restaurantsMap.values.toList()
        }
        return this
    }

    companion object {
        const val APPETIZERS = "appetizers"
    }
}