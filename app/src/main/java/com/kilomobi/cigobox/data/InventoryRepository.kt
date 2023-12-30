/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 30/12/2023 17:57.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data

import com.kilomobi.cigobox.BuildConfig
import com.kilomobi.cigobox.CigoBoxApplication
import com.kilomobi.cigobox.data.api.InventoryApiService
import com.kilomobi.cigobox.model.Appetizer
import com.kilomobi.cigobox.model.BoxOperation
import com.kilomobi.cigobox.model.PartialAppetizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException

class InventoryRepository {
    private var inventoryDao = InventoryDb.getDaoInstance(CigoBoxApplication.getAppContext())
    private val restInterface = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.DATABASE_URL)
        .build()
        .create(InventoryApiService::class.java)

    suspend fun getAllInventory(): List<Appetizer> {
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

    suspend fun updateLocalQuantity(id: Int, value: Int) {
        withContext(Dispatchers.IO) {
            inventoryDao.update(PartialAppetizer(id, value))
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