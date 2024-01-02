/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 02/01/2024 12:23.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain.usecase

import com.kilomobi.cigobox.data.InventoryRepository
import com.kilomobi.cigobox.domain.Appetizer
import javax.inject.Inject

class ValidateBoxWithdrawalUseCase @Inject constructor(
    private val repository: InventoryRepository,
    private val getAppetizersUseCase: GetAppetizersUseCase,
    private val getBoxOperationsUseCase: GetBoxOperationsUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
) {

    suspend operator fun invoke(appetizers: List<Appetizer>, playerCount: Int): List<Appetizer> {
        val boxOperations = getBoxOperationsUseCase(playerCount)
            .filter { it.withdrawQuantity > 0 }
        boxOperations.forEach {
            updateQuantityUseCase(appetizers, it.appetizerId, it.boxQuantity - it.withdrawQuantity)
        }
        repository.pushUpdatedOperations(boxOperations)
        return getAppetizersUseCase()
    }
}