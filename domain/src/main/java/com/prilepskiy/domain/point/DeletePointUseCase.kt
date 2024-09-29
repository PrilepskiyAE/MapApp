package com.prilepskiy.domain.point

import com.prilepskiy.data.repository.PointRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletePointUseCase @Inject constructor(private val pointRepository: PointRepository) {
    suspend operator fun invoke(latitude: Double, longitude: Double) {
        pointRepository.deletePoint(latitude, longitude)
    }
}
