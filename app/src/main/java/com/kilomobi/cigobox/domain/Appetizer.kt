/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.domain

data class Appetizer(
    val id: Int,
    val title: String,
    val supplier: String,
    val category: String,
    val quantity: Int,
    val bufferSize: Int,
    val isQuantityUpdated: Boolean = false,
    val isVisible: Boolean = true,
    val usedInBox: List<UsedInBox>
) {
    data class UsedInBox(
        val playerCount: Int,
        val boxQuantity: Int
    )
}