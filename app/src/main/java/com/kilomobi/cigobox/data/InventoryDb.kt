/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 22/12/2023 21:51.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kilomobi.cigobox.data.dao.InventoryDao
import com.kilomobi.cigobox.model.Appetizer

@Database(
    entities = [Appetizer::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Appetizer.UsedInBoxConverter::class)
abstract class InventoryDb : RoomDatabase() {
    abstract val dao: InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: InventoryDao? = null

        fun getDaoInstance(context: Context): InventoryDao {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = buildDatabase(context).dao
                    INSTANCE = instance
                }
                return instance
            }
        }

        private fun buildDatabase(context: Context): InventoryDb = Room.databaseBuilder(
            context.applicationContext,
            InventoryDb::class.java,
            "inventory_database"
        ).fallbackToDestructiveMigration().build()
    }
}