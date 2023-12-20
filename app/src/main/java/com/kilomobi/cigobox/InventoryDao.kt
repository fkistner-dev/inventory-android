package com.kilomobi.cigobox

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface InventoryDao {
    @Query("SELECT * FROM appetizers")
    suspend fun getAll(): List<Appetizer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(appetizers: List<Appetizer>)

    @Update(entity = Appetizer::class)
    suspend fun update(partialAppetizer: PartialAppetizer)
}