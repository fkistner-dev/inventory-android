/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data.api

import com.kilomobi.cigobox.model.Appetizer
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface InventoryApiService {
    @GET("appetizers.json")
    suspend fun getInventory(): List<Appetizer>

    @PUT("appetizers/{id}/a_quantity.json")
    suspend fun updateQuantity(@Path("id") id: Int, @Body quantity: Int): ResponseBody
}