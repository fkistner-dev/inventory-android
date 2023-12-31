/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 11:44.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

class ValidateEditStockUseCase {
    private val getAppetizersUseCase = GetAppetizersUseCase()
    private val getUpdatedAppetizersUseCase = GetUpdatedAppetizersUseCase()
    private val updateQuantityUseCase = UpdateQuantityUseCase()
    private val updateRemoteStockUseCase = UpdateRemoteStockUseCase()

    suspend operator fun invoke(
        appetizers: List<Appetizer>
    ): List<Appetizer> {
        val updatedAppetizers = getUpdatedAppetizersUseCase(appetizers)
        updatedAppetizers.forEach {
            updateQuantityUseCase(it.id, it.quantity)
        }
        updateRemoteStockUseCase(updatedAppetizers)
        return getAppetizersUseCase()
    }
}