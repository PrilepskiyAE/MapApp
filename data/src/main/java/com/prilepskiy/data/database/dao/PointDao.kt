package com.prilepskiy.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prilepskiy.data.database.entity.PointEntity

@Dao
interface PointDao {
    @Query("SELECT * FROM PointEntity")
    suspend fun getAll(): List<PointEntity>

    @Query("SELECT * FROM PointEntity WHERE latitude = :latitude AND longitude=:longitude")
    suspend fun getPoint(latitude: Double, longitude: Double): List<PointEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pointEntity: PointEntity)

    @Query("DELETE FROM PointEntity WHERE latitude = :latitude AND longitude=:longitude")
    suspend fun deletePoint(latitude: Double, longitude: Double)

    @Query("DELETE  FROM PointEntity")
    suspend fun deleteAll()
}
