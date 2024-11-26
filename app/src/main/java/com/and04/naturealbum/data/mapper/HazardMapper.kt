package com.and04.naturealbum.data.mapper

import com.and04.naturealbum.data.dto.GreenEyeDto
import com.and04.naturealbum.data.room.HazardAnalyzeStatus

object HazardMapper {
    fun mapToPassOrFail(greenEyeDto: GreenEyeDto): HazardAnalyzeStatus {
        val result = greenEyeDto.images?.firstOrNull() ?: return HazardAnalyzeStatus.FAIL

        val mostHeightScore = result.confidence
        val porn = result.result?.porn?.confidence
        val sexy = result.result?.sexy?.confidence

        return if (mostHeightScore == porn || mostHeightScore == sexy) {
            HazardAnalyzeStatus.FAIL
        } else {
            HazardAnalyzeStatus.PASS
        }
    }
}
