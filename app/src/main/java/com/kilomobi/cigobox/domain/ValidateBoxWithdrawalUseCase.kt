/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 12:24.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

import com.kilomobi.cigobox.data.InventoryRepository
import javax.inject.Inject

class ValidateBoxWithdrawalUseCase @Inject constructor(
    private val repository: InventoryRepository,
    private val getAppetizersUseCase: GetAppetizersUseCase,
    private val getBoxOperationsUseCase: GetBoxOperationsUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
) {

    suspend operator fun invoke(playerCount: Int): List<Appetizer> {
        val boxOperations = getBoxOperationsUseCase(playerCount)
            .filter { it.withdrawQuantity > 0 }
        boxOperations.forEach {
            updateQuantityUseCase(it.appetizerId, it.boxQuantity - it.withdrawQuantity)
        }
        repository.pushUpdatedOperations(boxOperations)
        return getAppetizersUseCase()
    }
}