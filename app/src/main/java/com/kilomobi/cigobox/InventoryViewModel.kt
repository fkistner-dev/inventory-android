package com.kilomobi.cigobox

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class InventoryViewModel(private val stateHandle: SavedStateHandle) : ViewModel() {
    val state = mutableStateOf(dummyAppetizers)
    val allowEdit = mutableStateOf(false)

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
            currentList.filter { it.category.name == category.name || it.category.name == Category.TOUT.name }
        state.value = currentList.map { item ->
            item.copy(isVisible = item in filteredList)
        }
    }

    fun validateStock() {
        toggleEdit()
    }
}