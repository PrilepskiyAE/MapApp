package com.prilepskiy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prilepskiy.common.VERSION_DATABASE
import com.prilepskiy.data.database.dao.PointDao
import com.prilepskiy.data.database.entity.PointEntity

@Database(
    entities = [
        PointEntity::class,
    ],
    version = VERSION_DATABASE
)
abstract class MapDataBase : RoomDatabase() {
    abstract fun pointDao(): PointDao
}