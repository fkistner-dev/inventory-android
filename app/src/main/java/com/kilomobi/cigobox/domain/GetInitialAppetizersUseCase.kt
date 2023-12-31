/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 00:36.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

import com.kilomobi.cigobox.data.InventoryRepository

class GetInitialAppetizersUseCase {
    private val repository: InventoryRepository = InventoryRepository()

    suspend operator fun invoke() : List<Appetizer> {
        repository.loadInventory()
        return repository.getInventory().sortedBy { it.title }
    }
}