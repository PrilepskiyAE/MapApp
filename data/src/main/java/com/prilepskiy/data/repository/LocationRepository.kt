package com.prilepskiy.data.repository

import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.prilepskiy.common.INTERVAL_MILLIS
import com.prilepskiy.common.ResourceProvider
import javax.inject.Inject
import javax.inject.Singleton


interface GeolocationCallback {

    fun onGeolocationReceived(data: Location?)

    fun onError(error: Throwable)
}

@Singleton
class LocationRepository @Inject constructor(private val resourceProvider: ResourceProvider) {

    private val locationProvider by lazy {
        LocationServices.getFusedLocationProviderClient(resourceProvider.getContext())
    }

    private var locationCallback: LocationCallback? = null
    private fun setUpLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_MILLIS,
        ).apply {
            setWaitForAccurateLocation(false)
            setMinUpdateIntervalMillis(IMPLICIT_MIN_UPDATE_INTERVAL)
            setMaxUpdateDelayMillis(INTERVAL_MILLIS)
        }.build()
        return locationRequest
    }

    fun fetchLocation(callback: GeolocationCallback) {
        try {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for (location in p0.locations) {
                        callback.onGeolocationReceived(location)
                    }
                }
            }

            locationCallback?.let {
                locationProvider.requestLocationUpdates(
                    setUpLocationRequest(),
                    it,
                    Looper.getMainLooper(),
                )
            }
        } catch (e: SecurityException) {
            callback.onError(e)
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            locationProvider.removeLocationUpdates(it)
        }
    }
}