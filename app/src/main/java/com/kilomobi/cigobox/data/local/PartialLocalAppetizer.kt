/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
class PartialLocalAppetizer(
    @ColumnInfo(name = "a_id")
    val id: Int,
    @ColumnInfo(name = "a_quantity")
    val quantity: Int
)