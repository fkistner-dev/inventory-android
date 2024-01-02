/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 02/01/2024 12:33.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain.usecase

import com.kilomobi.cigobox.data.InventoryRepository
import com.kilomobi.cigobox.domain.Appetizer
import javax.inject.Inject

class UpdateQuantityUseCase @Inject constructor(
    private val repository: InventoryRepository,
) {
    suspend operator fun invoke(
        currentAppetizers: List<Appetizer>,
        id: Int,
        quantity: Int
    ): List<Appetizer> {
        val appetizers = currentAppetizers.toMutableList()
        val itemIndex = appetizers.indexOfFirst { it.id == id }

        if (itemIndex != -1) {
            val item = appetizers[itemIndex]
            val newQuantity = quantity.coerceAtLeast(0)

            appetizers[itemIndex] = item.copy(
                quantity = newQuantity,
                isQuantityUpdated = true
            )

            repository.updateLocalQuantity(id, newQuantity)
        }

        return appetizers
    }
}