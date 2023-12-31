/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 11:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

import com.kilomobi.cigobox.data.InventoryRepository
import javax.inject.Inject

class UpdateQuantityUseCase @Inject constructor(
    private val repository: InventoryRepository,
    private val getAppetizersUseCase: GetAppetizersUseCase
) {
    suspend operator fun invoke(id: Int, quantity: Int): List<Appetizer> {
        val appetizers = getAppetizersUseCase().toMutableList()
        val itemIndex = appetizers.indexOfFirst { it.id == id }

        if (itemIndex != -1) {
            val item = appetizers[itemIndex]
            val newQuantity = quantity.coerceAtLeast(0)

            appetizers[itemIndex] =
                item.copy(
                    quantity = newQuantity,
                    isQuantityUpdated = true
                )

            repository.updateLocalQuantity(id, newQuantity)
        }

        return appetizers
    }
}