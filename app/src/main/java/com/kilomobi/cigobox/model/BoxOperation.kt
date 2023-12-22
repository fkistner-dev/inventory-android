/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.model

data class BoxOperation(
    val playerCount: Int,
    val appetizerId: Int,
    val title: String,
    val supplier: String,
    val boxQuantity: Int,
    val withdrawQuantity: Int
)
