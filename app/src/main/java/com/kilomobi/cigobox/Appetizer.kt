package com.kilomobi.cigobox

import com.google.gson.annotations.SerializedName

data class Appetizer(
    @SerializedName("a_id")
    val id: Int,
    @SerializedName("a_title")
    val title: String,
    @SerializedName("a_supplier")
    val supplier: String,
    @SerializedName("a_category")
    val category: String,
    @SerializedName("a_quantity")
    val quantity: Int,
    val isEditable: Boolean = false,
    val isVisible: Boolean = true,
    @SerializedName("a_usedInBox")
    val usedInBox: List<Int>? = listOf()
)