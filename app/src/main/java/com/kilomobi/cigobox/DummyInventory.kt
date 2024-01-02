/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 01/01/2024 19:49.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox

import com.kilomobi.cigobox.data.remote.RemoteAppetizer
import com.kilomobi.cigobox.domain.Appetizer
import com.kilomobi.cigobox.domain.BoxOperation
import com.kilomobi.cigobox.domain.Category

object DummyContent {
    fun getDomainAppetizers() = arrayListOf(
        Appetizer(0, "alcool0", "supplier0", Category.ALCOOL.name, 1, 2, usedInBox = listOf(Appetizer.UsedInBox(3, 1))),
        Appetizer(1, "alcool1", "supplier1", Category.ALCOOL.name, 2, 2, usedInBox = listOf(Appetizer.UsedInBox(3, 1))),
        Appetizer(2, "frais2", "supplier2", Category.FRAIS.name, 3, 2, usedInBox = listOf(Appetizer.UsedInBox(3, 1))),
        Appetizer(3, "sec3", "supplier3", Category.SEC.name, 4, 2, usedInBox = listOf(Appetizer.UsedInBox(3, 1)))
    )
    fun getRemoteInventory() = getDomainAppetizers().map { appetizer ->
        RemoteAppetizer(appetizer.id, appetizer.title, appetizer.supplier, appetizer.category, appetizer.quantity, appetizer.bufferSize, usedInBox = appetizer.usedInBox.map {
            RemoteAppetizer.RemoteUsedInBox(it.playerCount, it.boxQuantity)
        })
    }

    fun getBoxOperations() = getDomainAppetizers().map {
        BoxOperation(3, it.id, it.title, it.supplier, it.quantity, 1)
    }
}
