/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 01/01/2024 19:52.
 * Copyright (c) 2024.
 * All rights reserved.
 */

package com.kilomobi.cigobox

import com.kilomobi.cigobox.data.remote.InventoryApiService
import com.kilomobi.cigobox.data.remote.RemoteAppetizer
import kotlinx.coroutines.delay
import okhttp3.ResponseBody

class FakeApiService : InventoryApiService {
    override suspend fun getInventory(): List<RemoteAppetizer> {
        delay(1000)
        return DummyContent.getRemoteInventory()
    }

    override suspend fun updateQuantity(id: Int, quantity: Int): ResponseBody {
        TODO("Not yet implemented")
    }
}
