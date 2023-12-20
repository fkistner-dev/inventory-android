package com.kilomobi.cigobox

import retrofit2.Call
import retrofit2.http.GET

interface InventoryApiService {
    @GET("appetizers.json")
    fun getInventory(): Call<List<Appetizer>>
}