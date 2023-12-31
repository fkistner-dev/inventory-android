/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 12:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

import com.kilomobi.cigobox.data.InventoryRepository
import javax.inject.Inject

class GetAppetizersUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke() : List<Appetizer> {
        return repository.getInventory().sortedBy { it.title }
    }
}