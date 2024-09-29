package com.prilepskiy.data.repository

import com.prilepskiy.data.database.dao.PointDao
import com.prilepskiy.data.database.entity.PointEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointRepository @Inject constructor(private val pointDao: PointDao) {
    suspend fun getAll(): List<PointEntity> = pointDao.getAll()
    suspend fun getPoint(latitude: Double, longitude: Double): List<PointEntity> =
        pointDao.getPoint(latitude, longitude)

    suspend fun insertPoint(pointEntity: PointEntity) = pointDao.insert(pointEntity)
    suspend fun deletePoint(latitude: Double, longitude: Double) =
        pointDao.deletePoint(latitude, longitude)

    suspend fun deleteAll() = pointDao.deleteAll()
}
