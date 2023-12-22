/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

@Entity(tableName = "appetizers")
data class Appetizer(
    @PrimaryKey
    @ColumnInfo("a_id")
    @SerializedName("a_id")
    val id: Int,
    @ColumnInfo("a_title")
    @SerializedName("a_title")
    val title: String,
    @ColumnInfo("a_supplier")
    @SerializedName("a_supplier")
    val supplier: String,
    @ColumnInfo("a_category")
    @SerializedName("a_category")
    val category: String,
    @ColumnInfo("a_quantity")
    @SerializedName("a_quantity")
    val quantity: Int,
    @ColumnInfo("a_buffer_size")
    @SerializedName("a_buffer_size")
    val bufferSize: Int,
    val isQuantityUpdated: Boolean = false,
    val isVisible: Boolean = true,
    @TypeConverters(UsedInBoxConverter::class)
    @ColumnInfo(name = "a_usedInBox")
    @SerializedName("a_usedInBox")
    val usedInBox: List<UsedInBox>
) {
    data class UsedInBox(
        @SerializedName("player_count") val playerCount: Int,
        @SerializedName("box_quantity") val boxQuantity: Int
    )

    class UsedInBoxConverter {
        @TypeConverter
        fun fromString(value: String): List<UsedInBox> {
            val listType = object : TypeToken<List<UsedInBox>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun toString(value: List<UsedInBox>): String {
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
        if (isQuantityUpdated != other.isQuantityUpdated) return false
        if (isVisible != other.isVisible) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + supplier.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + quantity
        result = 31 * result + isQuantityUpdated.hashCode()
        result = 31 * result + isVisible.hashCode()
        return result
    }
}