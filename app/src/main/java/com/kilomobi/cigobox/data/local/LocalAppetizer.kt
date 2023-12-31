/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 01:02.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.kilomobi.cigobox.domain.Appetizer

@Entity(tableName = "appetizers")
data class LocalAppetizer(
    @PrimaryKey
    @ColumnInfo("a_id")
    val id: Int,
    @ColumnInfo("a_title")
    val title: String,
    @ColumnInfo("a_supplier")
    val supplier: String,
    @ColumnInfo("a_category")
    val category: String,
    @ColumnInfo("a_quantity")
    val quantity: Int,
    @ColumnInfo("a_buffer_size")
    val bufferSize: Int,
    @TypeConverters(UsedInBoxConverter::class)
    @ColumnInfo(name = "a_usedInBox")
    val usedInBox: List<LocalUsedInBox>
) {
    data class LocalUsedInBox(
        @ColumnInfo("player_count") val playerCount: Int,
        @ColumnInfo("box_quantity") val boxQuantity: Int
    )

    class UsedInBoxConverter {
        @TypeConverter
        fun fromString(value: String): List<LocalUsedInBox> {
            val listType = object : TypeToken<List<LocalUsedInBox>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun toString(value: List<LocalUsedInBox>): String {
            return Gson().toJson(value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Appetizer

        if (id != other.id) return false
        if (title != other.title) return false
        if (supplier != other.supplier) return false
        if (category != other.category) return false
        if (quantity != other.quantity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + supplier.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + quantity
        return result
    }
}