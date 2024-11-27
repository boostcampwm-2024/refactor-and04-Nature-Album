package com.and04.naturealbum.data.repository

import android.util.Log
import com.and04.naturealbum.data.dto.GreenEyeRequestBody
import com.and04.naturealbum.data.dto.GreenEyeRequestBodyImages
import com.and04.naturealbum.data.dto.ReverseGeocodeDto
import com.and04.naturealbum.data.mapper.HazardMapper
import com.and04.naturealbum.data.retorifit.NaverApi
import com.and04.naturealbum.data.room.HazardAnalyzeStatus
import com.and04.naturealbum.di.GreenEye
import com.and04.naturealbum.di.ReverseGeocode
import java.io.IOException
import javax.inject.Inject

interface RetrofitRepository {
    suspend fun convertCoordsToAddress(coords: String): Result<ReverseGeocodeDto>
    suspend fun analyzeHazardWithGreenEye(photoData: String): HazardAnalyzeStatus
}

class RetrofitRepositoryImpl @Inject constructor(
    @ReverseGeocode private val reverseGeocodeAPI: NaverApi,
    @GreenEye private val greenEyeAPI: NaverApi,
) :
    RetrofitRepository {

    override suspend fun convertCoordsToAddress(coords: String): Result<ReverseGeocodeDto> {
        return runRemote {
            reverseGeocodeAPI.convertCoordsToAddress(coords = coords)
        }
    }

    override suspend fun analyzeHazardWithGreenEye(photoData: String): HazardAnalyzeStatus {
        val result = runRemote {
            greenEyeAPI.analyzeHazardWithGreenEye(
                requestBody = GreenEyeRequestBody(
                    timestamp = System.currentTimeMillis(),
                    images = listOf(
                        GreenEyeRequestBodyImages(data = photoData)
                    )
                )
            )
        }
        return result.fold(
            onSuccess = { greenEyeDto -> HazardMapper.mapToPassOrFail(greenEyeDto) },
            onFailure = { HazardAnalyzeStatus.FAIL }
        )
    }

    private suspend fun <T> runRemote(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block.invoke())
        } catch (e: retrofit2.HttpException) { // HTTP 응답 코드가 4xx 또는 5xx일 때
            Log.e("ERROR", "HTTP Exception: ${e.code()} - ${e.message()}")
            Result.failure(e)
        } catch (e: IOException) { // 네트워크 연결 문제일 때
            Log.e("ERROR", "Network Error: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("ERROR", "Unknown Error: ${e.message}")
            Result.failure(e)
        }
    }
}
