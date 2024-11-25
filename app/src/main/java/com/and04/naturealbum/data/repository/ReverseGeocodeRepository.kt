package com.and04.naturealbum.data.repository

import android.util.Log
import com.and04.naturealbum.data.dto.ReverseGeocodeDto
import com.and04.naturealbum.data.retorifit.ReverseGeocodeAPI
import java.io.IOException
import javax.inject.Inject

interface ReverseGeocodeRepository {
    suspend fun convertCoordsToAddress(coords: String): Result<ReverseGeocodeDto>
}

class ReverseGeocodeRepositoryImpl @Inject constructor(private val reverseGeocodeAPI: ReverseGeocodeAPI) :
    ReverseGeocodeRepository {

    override suspend fun convertCoordsToAddress(coords: String): Result<ReverseGeocodeDto> {
        return runRemote {
            reverseGeocodeAPI.convertCoordsToAddress(coords = coords)
        }
    }

    private suspend fun <T> runRemote(block: suspend () -> T): Result<T> {
        return try {
            Log.d("F11", "${block.invoke().toString()}")
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
