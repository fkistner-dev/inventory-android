package com.kilomobi.cigobox

import retrofit2.http.GET

interface InventoryApiService {
    @GET("appetizers.json")
    suspend fun getInventory(): List<Appetizer>
}