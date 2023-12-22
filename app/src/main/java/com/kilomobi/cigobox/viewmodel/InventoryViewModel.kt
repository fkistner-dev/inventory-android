/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilomobi.cigobox.BuildConfig
import com.kilomobi.cigobox.CigoBoxApplication
import com.kilomobi.cigobox.data.InventoryDb
import com.kilomobi.cigobox.data.api.InventoryApiService
import com.kilomobi.cigobox.model.Appetizer
import com.kilomobi.cigobox.model.BoxOperation
import com.kilomobi.cigobox.model.Category
import com.kilomobi.cigobox.model.PartialAppetizer
import com.kilomobi.cigobox.model.toCategory
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
    val isBoxScreen = mutableStateOf(false)
    val selectedFilter = mutableStateOf(Category.TOUT)
    var selectedPlayerBox = mutableStateOf(0)

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.DATABASE_URL)
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
            var newQuantity = quantityChange.coerceAtLeast(0)
            if (allowEdit.value) {
                // allow decrease
                newQuantity = (item.quantity + quantityChange).coerceAtLeast(0)

            }
            appetizers[itemIndex] =
                item.copy(
                    quantity = newQuantity,
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
    fun subtractBox() {
        isBoxScreen.value = !isBoxScreen.value
    }

    fun getBoxOperationList(playerCount: Int): List<BoxOperation> {
        val currentList = items.value
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
        allowEdit.value = !allowEdit.value
    }

    private fun toggleReset() {
        allowEdit.value = false
        isBoxScreen.value = false
        selectedPlayerBox.value = 0
    }

    // Used to filters between categories, and return a list with correct items visibility
    fun filterAction(category: Category) {
        val currentList = items.value
        val filteredList =
            currentList.filter { it.category.toCategory().name == category.name || category.name == Category.TOUT.name }
        val visibleList = currentList.map { item ->
            item.copy(isVisible = item in filteredList)
        }
        selectedFilter.value = category
        items.value = visibleList
    }

    fun selectBoxAction(playerCount: Int) {
        selectedPlayerBox.value = playerCount
    }

    // Update each modified item on remote db and handle toggles
    fun validateStock() {
        if (allowEdit.value) {
            viewModelScope.launch(errorHandler) {
                pushUpdatedQuantities(items.value.filter { it.isQuantityUpdated })
            }
        } else if (isBoxScreen.value) {
            viewModelScope.launch(errorHandler) {
                pushUpdatedOperations(getBoxOperationList(selectedPlayerBox.value)
                    .filter { it.withdrawQuantity > 0 }
                )
            }
        }

        // Disable change
        toggleReset()
    }

    private suspend inline fun <reified T : Any> pushUpdatedQuantities(
        modifiedValues: List<T>,
        crossinline getId: (T) -> Int,
        crossinline getQuantity: (T) -> Int
    ) {
        withContext(Dispatchers.IO) {
            modifiedValues.forEach {
                updateQuantity(getId(it), getQuantity(it))
                updateRemoteQuantity(getId(it), getQuantity(it).coerceAtLeast(0))
            }
        }
    }

    private suspend fun pushUpdatedOperations(modifiedValues: List<BoxOperation>) {
        pushUpdatedQuantities(
            modifiedValues,
            { it.appetizerId },
            { it.boxQuantity - it.withdrawQuantity })
    }

    private suspend fun pushUpdatedQuantities(modifiedValues: List<Appetizer>) {
        pushUpdatedQuantities(modifiedValues, { it.id }, { it.quantity })
    }
}