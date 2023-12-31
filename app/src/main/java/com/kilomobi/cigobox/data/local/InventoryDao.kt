/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 01:11.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface InventoryDao {
    @Query("SELECT * FROM appetizers")
    suspend fun getAll(): List<LocalAppetizer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(appetizers: List<LocalAppetizer>)

    @Update(entity = LocalAppetizer::class)
    suspend fun update(partialLocalAppetizer: PartialLocalAppetizer)
}