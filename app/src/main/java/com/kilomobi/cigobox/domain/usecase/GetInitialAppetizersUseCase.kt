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

class GetInitialAppetizersUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(): List<Appetizer> {
        repository.loadInventory()
        return repository.getInventory().sortedBy { it.title }
    }
}