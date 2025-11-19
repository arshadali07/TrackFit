package com.project.trackfit.model.mapper

import com.project.trackfit.model.client.run.RunRequestDto
import model.server.run.RunResponse
import java.time.Instant

fun RunRequestDto.toRunResponse(mapPictureUrl: String?): RunResponse {
    return RunResponse(
        id = id,
        dateTimeUtc = Instant.now().toString(),
        durationMillis = durationMillis,
        distanceMeters = distanceMeters,
        lat = lat,
        long = long,
        avgSpeedKmh = avgSpeedKmh,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate
    )
}