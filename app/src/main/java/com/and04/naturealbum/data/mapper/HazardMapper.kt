package com.and04.naturealbum.data.mapper

import com.and04.naturealbum.data.dto.GreenEyeDto

object HazardMapper {
    fun mapToPassOrFail(greenEyeDto: GreenEyeDto): HazardMapperResult {
        val result = greenEyeDto.images?.firstOrNull() ?: return HazardMapperResult.FAIL

        val mostHeightScore = result.confidence
        val porn = result.result?.porn?.confidence
        val sexy = result.result?.sexy?.confidence

        return if (mostHeightScore == porn || mostHeightScore == sexy) {
            HazardMapperResult.FAIL
        } else {
            HazardMapperResult.PASS
        }
    }
}

enum class HazardMapperResult {
    PASS,
    FAIL
}
