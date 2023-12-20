package com.kilomobi.cigobox

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val isQuantityUpdated: Boolean = false,
    val isEditable: Boolean = false,
    val isVisible: Boolean = true,
    @ColumnInfo("a_usedInBox")
    @SerializedName("a_usedInBox")
    val usedInBox: String
)