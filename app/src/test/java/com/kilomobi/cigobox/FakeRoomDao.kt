/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 01/01/2024 20:11.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox

import com.kilomobi.cigobox.data.local.InventoryDao
import com.kilomobi.cigobox.data.local.LocalAppetizer
import com.kilomobi.cigobox.data.local.PartialLocalAppetizer
import kotlinx.coroutines.delay

class FakeRoomDao : InventoryDao {
    private var appetizers = HashMap<Int, LocalAppetizer>()
    override suspend fun getAll()
            : List<LocalAppetizer> {
        delay(1000)
        return appetizers.values.toList()
    }

    override suspend fun addAll(
        appetizers: List<LocalAppetizer>
    ) {
        appetizers.forEach { this.appetizers[it.id] = it }
    }

    override suspend fun update(
        partialLocalAppetizer: PartialLocalAppetizer
    ) {
        delay(1000)
        updateAppetizer(partialLocalAppetizer)
    }

    private fun updateAppetizer(
        partialLocalAppetizer: PartialLocalAppetizer
    ) {
        val appetizer = this.appetizers[partialLocalAppetizer.id]
        if (appetizer != null)
            this.appetizers[partialLocalAppetizer.id] =
                appetizer.copy(quantity = partialLocalAppetizer.quantity)
    }
}
