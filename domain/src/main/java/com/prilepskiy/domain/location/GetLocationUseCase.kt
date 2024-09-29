package com.prilepskiy.domain.location

import android.location.Location
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.prilepskiy.common.Failure
import com.prilepskiy.data.repository.GeolocationCallback
import com.prilepskiy.data.repository.LocationRepository
import com.prilepskiy.domain.mapper.locationToGeolocationModel
import com.prilepskiy.domain.model.GeolocationFetchFailure
import com.prilepskiy.domain.model.GeolocationModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GetLocationUseCase @Inject constructor(private val locationRepository: LocationRepository) {
    operator fun invoke() = callbackFlow<Either<Failure, GeolocationModel>> {
        locationRepository.fetchLocation(
            object : GeolocationCallback {
                override fun onGeolocationReceived(data: Location?) {
                    trySend(data.locationToGeolocationModel().right())
                }

                override fun onError(error: Throwable) {
                    trySend(
                        GeolocationFetchFailure(
                            error
                        ).left()
                    )
                }
            }
        )

        awaitClose {
            locationRepository.stopLocationUpdates()
        }
    }
}
