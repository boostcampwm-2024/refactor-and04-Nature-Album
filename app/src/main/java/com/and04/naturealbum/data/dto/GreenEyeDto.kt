package com.and04.naturealbum.data.dto

data class GreenEyeDto(
    val version: String?,
    val requestId: String?,
    val timestamp: Long?,
    val images: List<GreenEyeImage>?,
)


data class GreenEyeImage(
    val result: GreenEyeResult?,
    val latency: Double?,
    val confidence: Double?,
    val message: String?,
    val name: String?,
)


data class GreenEyeResult(
    val adult: ConfidenceLevel?,
    val normal: ConfidenceLevel?,
    val porn: ConfidenceLevel?,
    val sexy: ConfidenceLevel?,
)

data class ConfidenceLevel(
    val confidence: Double,
)
