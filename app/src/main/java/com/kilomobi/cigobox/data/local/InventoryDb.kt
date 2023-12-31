/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 01:04.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [LocalAppetizer::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(LocalAppetizer.UsedInBoxConverter::class)
abstract class InventoryDb : RoomDatabase() {
    abstract val dao: InventoryDao
}