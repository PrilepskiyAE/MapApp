package com.prilepskiy.domain.model

data class PointModel (
    val pointId: Long = 0L,
    val title: String,
    val latitude: Double,
    val longitude: Double,
)
