package com.prilepskiy.data.database.di

import com.prilepskiy.data.database.MapDataBase
import com.prilepskiy.data.database.dao.PointDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PointDaoModule {
    @Provides
    fun provideMessageDao(database: MapDataBase): PointDao =
        database.pointDao()
}
