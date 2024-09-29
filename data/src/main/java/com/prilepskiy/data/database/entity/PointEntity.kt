package com.prilepskiy.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class PointEntity (
    @PrimaryKey(autoGenerate = true)
    val pointId: Long = 0L,
    val title: String,
    val latitude: Double,
    val longitude: Double,
)