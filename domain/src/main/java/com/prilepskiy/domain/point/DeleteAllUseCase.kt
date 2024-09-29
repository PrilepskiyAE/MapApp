package com.prilepskiy.domain.point

import com.prilepskiy.data.repository.PointRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAllUseCase @Inject constructor(private val pointRepository: PointRepository) {
    suspend operator fun invoke() {
        pointRepository.deleteAll()
    }
}
