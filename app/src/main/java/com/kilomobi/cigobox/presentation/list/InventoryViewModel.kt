/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 01:07.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.presentation.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilomobi.cigobox.domain.usecase.GetInitialAppetizersUseCase
import com.kilomobi.cigobox.domain.BoxOperation
import com.kilomobi.cigobox.domain.Category
import com.kilomobi.cigobox.domain.usecase.GetFilteredAppetizersUseCase
import com.kilomobi.cigobox.domain.usecase.UpdateQuantityUseCase
import com.kilomobi.cigobox.domain.usecase.ValidateBoxWithdrawalUseCase
import com.kilomobi.cigobox.domain.usecase.ValidateEditStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInitialAppetizersUseCase: GetInitialAppetizersUseCase,
    private val getFilteredAppetizersUseCase: GetFilteredAppetizersUseCase,
    private val validateEditStockUseCase: ValidateEditStockUseCase,
    private val validateBoxWithdrawalUseCase: ValidateBoxWithdrawalUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase

): ViewModel() {
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
        loadInventory()
    }

    fun loadInventory() {
        viewModelScope.launch(errorHandler) {
            val remoteList = getInitialAppetizersUseCase()
            val appetizerList = remoteList.map { it.copy(isVisible = true) }
            _state.value = _state.value.copy(
                appetizers = appetizerList,
                isLoading = false,
                error = null
            )
        }
    }

    fun updateQuantity(id: Int, quantityChange: Int) {
        viewModelScope.launch(errorHandler) {
            val appetizers = updateQuantityUseCase(_state.value.appetizers, id, quantityChange)
            _state.value = _state.value.copy(
                appetizers = appetizers
            )
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
        viewModelScope.launch(errorHandler) {
            _state.value = _state.value.copy(
                appetizers = getFilteredAppetizersUseCase(category),
                selectedFilter = category
            )
        }
    }

    fun selectBoxAction(playerCount: Int) {
        _state.value = _state.value.copy(
            selectedPlayerBox = playerCount,
        )
    }

    // Update each modified item on remote db and handle toggles
    fun validateStock() {
        viewModelScope.launch(errorHandler) {
            var appetizers = _state.value.appetizers

            if (_state.value.allowEdit) {
                appetizers = validateEditStockUseCase(appetizers)
            } else if (_state.value.isBoxScreen) {
                appetizers = validateBoxWithdrawalUseCase(appetizers, _state.value.selectedPlayerBox)
            }

            // Refresh appetizers state
            _state.value = _state.value.copy(
                appetizers = appetizers
            )

            // Disable change
            toggleReset()
        }
    }
}