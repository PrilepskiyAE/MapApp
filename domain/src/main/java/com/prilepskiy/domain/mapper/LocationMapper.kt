package com.prilepskiy.domain.mapper

import android.location.Location
import com.prilepskiy.common.DEFAULT_LOCATION
import com.prilepskiy.domain.model.GeolocationModel

fun Location?.locationToGeolocationModel() = GeolocationModel(
    latitude = this?.latitude ?: DEFAULT_LOCATION,
    longitude = this?.longitude ?: DEFAULT_LOCATION,
)
