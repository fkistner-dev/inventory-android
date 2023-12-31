/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 11:57.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

import com.kilomobi.cigobox.data.InventoryRepository

class UpdateRemoteStockUseCase {
    private val repository: InventoryRepository = InventoryRepository()

    suspend operator fun invoke(appetizers: List<Appetizer>) {
        repository.pushUpdatedQuantities(appetizers)
    }
}