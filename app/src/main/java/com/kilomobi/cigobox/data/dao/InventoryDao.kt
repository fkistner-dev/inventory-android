/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kilomobi.cigobox.model.Appetizer
import com.kilomobi.cigobox.model.PartialAppetizer

@Dao
interface InventoryDao {
    @Query("SELECT * FROM appetizers")
    suspend fun getAll(): List<Appetizer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(appetizers: List<Appetizer>)

    @Update(entity = Appetizer::class)
    suspend fun update(partialAppetizer: PartialAppetizer)
}