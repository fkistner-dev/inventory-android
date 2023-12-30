/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilomobi.cigobox.data.InventoryRepository
import com.kilomobi.cigobox.ui.screen.InventoryScreenState
import com.kilomobi.cigobox.model.BoxOperation
import com.kilomobi.cigobox.model.Category
import com.kilomobi.cigobox.model.toCategory
import kotlinx.coroutines.*

class InventoryViewModel : ViewModel() {
    private val repository = InventoryRepository()
    private val _state = mutableStateOf(
        InventoryScreenState(
        appetizers = emptyList(),
        isLoading = true,
        allowEdit = false,
        isBoxScreen = false,
        selectedFilter = Category.TOUT,
        selectedPlayerBox = 0
    )
    )

    val state: State<InventoryScreenState>
        get() = _state

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        _state.value = _state.value.copy(
            error = exception.message,
            isLoading = false
        )
    }

    init {
        initializeInventory()
    }

    fun initializeInventory() {
        getInventory()
    }

    private fun getInventory() {
        viewModelScope.launch(errorHandler) {
            val remoteList = repository.getAllInventory()
            val appetizerList = remoteList.map { it.copy(isVisible = true) }
            _state.value = _state.value.copy(
                appetizers = appetizerList,
                isLoading = false,
                error = null
            )
        }
    }

    fun updateQuantity(id: Int, quantityChange: Int) {
        val appetizers = _state.value.appetizers.toMutableList()
        val itemIndex = appetizers.indexOfFirst { it.id == id }

        if (itemIndex != -1) {
            val item = appetizers[itemIndex]
            val newQuantity = quantityChange.coerceAtLeast(0)

            appetizers[itemIndex] =
                item.copy(
                    quantity = newQuantity,
                    isQuantityUpdated = true
                )
            _state.value = _state.value.copy(
                appetizers = appetizers
            )
            viewModelScope.launch {
                repository.updateLocalQuantity(id, item.quantity)
            }
        }
    }

    // Handle a bunch of items to subtract within a box
    fun subtractBox() {
        _state.value = _state.value.copy(
            isBoxScreen = !_state.value.isBoxScreen
        )
    }

    fun getBoxOperationList(playerCount: Int): List<BoxOperation> {
        val currentList = _state.value.appetizers
        val boxOperationList = mutableListOf<BoxOperation>()

        currentList.forEach { appetizer ->
            appetizer.usedInBox.find { it.playerCount == playerCount }?.let { usedInBox ->
                // Add a BoxOperation for each appetizer used in the box
                boxOperationList.add(
                    BoxOperation(
                        playerCount = playerCount,
                        title = appetizer.title,
                        appetizerId = appetizer.id,
                        supplier = appetizer.supplier,
                        boxQuantity = appetizer.quantity,
                        withdrawQuantity = usedInBox.boxQuantity
                    )
                )
            }
        }

        return boxOperationList
    }

    fun toggleEdit() {
        _state.value = _state.value.copy(
            allowEdit = !_state.value.allowEdit
        )
    }

    private fun toggleReset() {
        _state.value = _state.value.copy(
            allowEdit = false,
            isBoxScreen = false,
            selectedPlayerBox = 0
        )
    }

    // Used to filters between categories, and return a list with correct items visibility
    fun filterAction(category: Category) {
        val currentList = _state.value.appetizers
        val filteredList =
            currentList.filter { it.category.toCategory().name == category.name || category.name == Category.TOUT.name }
        val visibleList = currentList.map { item ->
            item.copy(isVisible = item in filteredList)
        }
        _state.value = _state.value.copy(
            appetizers = visibleList,
            selectedFilter = category
        )
    }

    fun selectBoxAction(playerCount: Int) {
        _state.value = _state.value.copy(
            selectedPlayerBox = playerCount,
        )
    }

    // Update each modified item on remote db and handle toggles
    fun validateStock() {
        if (_state.value.allowEdit) {
            viewModelScope.launch(errorHandler) {
                val appetizers = _state.value.appetizers.filter { it.isQuantityUpdated }
                appetizers.forEach {
                    updateQuantity(it.id, it.quantity)
                }
                repository.pushUpdatedQuantities(appetizers)
            }
        } else if (_state.value.isBoxScreen) {
            viewModelScope.launch(errorHandler) {
                val boxOperations = getBoxOperationList(_state.value.selectedPlayerBox)
                    .filter { it.withdrawQuantity > 0 }
                boxOperations.forEach {
                    updateQuantity(it.appetizerId, it.boxQuantity - it.withdrawQuantity)
                }
                repository.pushUpdatedOperations(boxOperations)
            }
        }

        // Disable change
        toggleReset()
    }
}