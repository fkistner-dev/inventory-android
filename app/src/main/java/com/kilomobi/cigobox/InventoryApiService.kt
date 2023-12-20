package com.kilomobi.cigobox

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