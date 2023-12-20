package com.kilomobi.cigobox

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
class PartialAppetizer(
    @ColumnInfo(name = "a_id")
    val id: Int,
    @ColumnInfo(name = "a_quantity")
    val quantity: Int
)