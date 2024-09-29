package com.prilepskiy.domain.model

import com.prilepskiy.common.DEFAULT_LOCATION

data class GeolocationModel(
    val latitude: Double = DEFAULT_LOCATION,
    val longitude: Double = DEFAULT_LOCATION,
)
