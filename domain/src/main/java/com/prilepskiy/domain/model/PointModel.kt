package com.prilepskiy.domain.model

import com.prilepskiy.common.DEFAULT_LOCATION
import com.prilepskiy.common.EMPTY_STRING

data class PointModel (
    val pointId: Long = 0L,
    val title: String = EMPTY_STRING,
    val latitude: Double= DEFAULT_LOCATION,
    val longitude: Double= DEFAULT_LOCATION,
)
