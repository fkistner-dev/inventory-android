/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 02/01/2024 12:21.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain.usecase

import com.kilomobi.cigobox.domain.Appetizer
import javax.inject.Inject

class ValidateEditStockUseCase @Inject constructor(
    private val getAppetizersUseCase: GetAppetizersUseCase,
    private val getUpdatedAppetizersUseCase: GetUpdatedAppetizersUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val updateRemoteStockUseCase: UpdateRemoteStockUseCase
) {
    suspend operator fun invoke(
        appetizers: List<Appetizer>
    ): List<Appetizer> {
        val updatedAppetizers = getUpdatedAppetizersUseCase(appetizers)
        updatedAppetizers.forEach {
            updateQuantityUseCase(updatedAppetizers, it.id, it.quantity)
        }
        updateRemoteStockUseCase(updatedAppetizers)
        return getAppetizersUseCase()
    }
}