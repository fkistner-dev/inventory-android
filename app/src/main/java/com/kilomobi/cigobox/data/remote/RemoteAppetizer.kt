/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 01:01.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data.remote

import com.google.gson.annotations.SerializedName

data class RemoteAppetizer(
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
    @SerializedName("a_buffer_size")
    val bufferSize: Int,
    val isQuantityUpdated: Boolean = false,
    val isVisible: Boolean = true,
    @SerializedName("a_usedInBox")
    val usedInBox: List<RemoteUsedInBox>
) {
    data class RemoteUsedInBox(
        @SerializedName("player_count") val playerCount: Int,
        @SerializedName("box_quantity") val boxQuantity: Int
    )
}
