/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 15:41.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain.usecase

import com.kilomobi.cigobox.domain.BoxOperation
import javax.inject.Inject

class GetBoxOperationsUseCase @Inject constructor(
    private val getInitialAppetizersUseCase: GetInitialAppetizersUseCase
) {
    suspend operator fun invoke(playerCount: Int): List<BoxOperation> {
        val currentList = getInitialAppetizersUseCase()
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
}