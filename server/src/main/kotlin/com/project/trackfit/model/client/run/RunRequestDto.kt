package com.project.trackfit.model.client.run

import kotlinx.serialization.Serializable

@Serializable
data class RunRequestDto(
    val epochMillis: Long?,
    val id: String?,
    val durationMillis: Long?,
    val distanceMeters: Int?,
    val lat: Double?,
    val long: Double?,
    val avgSpeedKmh: Double?,
    val maxSpeedKmh: Double?,
    val totalElevationMeters: Int?,
    val avgHeartRate: Int?,
    val maxHeartRate: Int?
)
