package com.prilepskiy.domain.mapper

import com.prilepskiy.data.database.entity.PointEntity
import com.prilepskiy.domain.model.PointModel

fun PointModel.asEntity(): PointEntity = PointEntity(
    pointId = pointId,
    title = title,
    latitude = latitude,
    longitude = longitude
)

fun PointEntity.asModel() = PointModel(
    pointId = pointId,
    title = title,
    latitude = latitude,
    longitude = longitude
)