package com.and04.naturealbum.data.repository

import android.util.Log
import com.and04.naturealbum.data.GreenEyeRequestBody
import com.and04.naturealbum.data.GreenEyeRequestBodyImages
import com.and04.naturealbum.data.dto.GreenEyeDto
import com.and04.naturealbum.data.dto.ReverseGeocodeDto
import com.and04.naturealbum.data.retorifit.NaverApi
import com.and04.naturealbum.di.GreenEye
import com.and04.naturealbum.di.ReverseGeocode
import java.io.IOException
import javax.inject.Inject

interface RetrofitRepository {
    suspend fun convertCoordsToAddress(coords: String): Result<ReverseGeocodeDto>
    suspend fun analyzeForToxicity(): Result<GreenEyeDto>
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

    override suspend fun analyzeForToxicity(): Result<GreenEyeDto> {
        return runRemote {
            greenEyeAPI.analyzeHazardWithGreenEye(
                requestBody = GreenEyeRequestBody(
                    timestamp = System.currentTimeMillis(),
                    images = listOf(GreenEyeRequestBodyImages(name = "", data = ""))
                )
            )
        }
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
