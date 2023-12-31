/*
 * Created by fkistner.
 * fabrice.kistner.pro@gmail.com
 * Last modified on 31/12/2023 15:26.
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.kilomobi.cigobox.data.di

import android.content.Context
import androidx.room.Room
import com.kilomobi.cigobox.BuildConfig
import com.kilomobi.cigobox.data.local.InventoryDao
import com.kilomobi.cigobox.data.local.InventoryDb
import com.kilomobi.cigobox.data.remote.InventoryApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventoryModule {
    @Singleton
    @Provides
    fun provideRoomDao(database: InventoryDb): InventoryDao {
        return database.dao
    }

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext appContext: Context
    ): InventoryDb {
        return Room.databaseBuilder(
            appContext.applicationContext,
            InventoryDb::class.java,
            "inventory_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.DATABASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitApi(retrofit: Retrofit): InventoryApiService {
        return retrofit.create(InventoryApiService::class.java)
    }
}