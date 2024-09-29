package com.prilepskiy.domain.point

import com.prilepskiy.data.repository.PointRepository
import com.prilepskiy.domain.mapper.asEntity
import com.prilepskiy.domain.model.PointModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddPointUseCase @Inject constructor(private val pointRepository: PointRepository) {
    suspend operator fun invoke(pointModel: PointModel) {
        pointRepository.insertPoint(pointModel.asEntity())
    }
}
