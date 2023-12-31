/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 30/12/2023 17:57.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data

import com.kilomobi.cigobox.data.local.InventoryDao
import com.kilomobi.cigobox.data.remote.InventoryApiService
import com.kilomobi.cigobox.domain.Appetizer
import com.kilomobi.cigobox.domain.BoxOperation
import com.kilomobi.cigobox.data.local.LocalAppetizer
import com.kilomobi.cigobox.data.local.PartialLocalAppetizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val restInterface: InventoryApiService,
    private var inventoryDao: InventoryDao
) {
    suspend fun getInventory(): List<Appetizer> {
        return withContext(Dispatchers.IO) {
            return@withContext inventoryDao.getAll().map {
                Appetizer(
                    it.id,
                    it.title,
                    it.supplier,
                    it.category,
                    it.quantity,
                    it.bufferSize,
                    isQuantityUpdated = false,
                    isVisible = true,
                    usedInBox = it.usedInBox.map { box ->
                        Appetizer.UsedInBox(
                            box.playerCount,
                            box.boxQuantity
                        )
                    })
            }
        }
    }

    suspend fun loadInventory() {
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
        }
    }

    private suspend fun refreshCache() {
        val remoteInventory = restInterface.getInventory()
        inventoryDao.addAll(remoteInventory.map {
            LocalAppetizer(
                it.id,
                it.title,
                it.supplier,
                it.category,
                it.quantity,
                it.bufferSize,
                it.usedInBox.map { box ->
                    LocalAppetizer.LocalUsedInBox(
                        box.playerCount,
                        box.boxQuantity
                    )
                }
            )
        })
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

    suspend fun updateLocalQuantity(id: Int, value: Int) {
        withContext(Dispatchers.IO) {
            inventoryDao.update(PartialLocalAppetizer(id, value))
            // Retrieve the content of our local db
            inventoryDao.getAll()
        }
    }

    private suspend inline fun <reified T : Any> pushUpdatedQuantities(
        modifiedValues: List<T>,
        crossinline getId: (T) -> Int,
        crossinline getQuantity: (T) -> Int
    ) {
        withContext(Dispatchers.IO) {
            modifiedValues.forEach {
                updateRemoteQuantity(getId(it), getQuantity(it).coerceAtLeast(0))
            }
        }
    }

    suspend fun pushUpdatedOperations(modifiedValues: List<BoxOperation>) {
        pushUpdatedQuantities(
            modifiedValues,
            { it.appetizerId },
            { it.boxQuantity - it.withdrawQuantity })
    }

    suspend fun pushUpdatedQuantities(modifiedValues: List<Appetizer>) {
        pushUpdatedQuantities(modifiedValues, { it.id }, { it.quantity })
    }
}