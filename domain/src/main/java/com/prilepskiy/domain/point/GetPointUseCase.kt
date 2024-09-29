package com.prilepskiy.domain.point

import com.prilepskiy.data.repository.PointRepository
import com.prilepskiy.domain.mapper.asModel
import com.prilepskiy.domain.model.PointModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPointUseCase @Inject constructor(private val pointRepository: PointRepository) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double
    ): List<PointModel> {
        return pointRepository.getPoint(latitude, longitude).map { it.asModel() }
    }
}
