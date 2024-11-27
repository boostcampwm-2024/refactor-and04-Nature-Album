package com.and04.naturealbum.data.retorifit

import com.and04.naturealbum.BuildConfig.NAVER_EYE_DOMAIN_ID
import com.and04.naturealbum.BuildConfig.NAVER_EYE_SIGNATURE
import com.and04.naturealbum.data.dto.GreenEyeDto
import com.and04.naturealbum.data.dto.GreenEyeRequestBody
import com.and04.naturealbum.data.dto.ReverseGeocodeDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NaverApi {

    @GET("map-reversegeocode/v2/gc")
    suspend fun convertCoordsToAddress(
        @Query(value = "coords", encoded = true) coords: String,
        @Query("output") output: String? = "json",
        @Query("orders") orders: String? = "roadaddr",
    ): ReverseGeocodeDto

    @POST("custom/v1/{domainId}/{signature}/predict")
    suspend fun analyzeHazardWithGreenEye(
        @Path("domainId") domainId: String = NAVER_EYE_DOMAIN_ID,
        @Path("signature") signature: String = NAVER_EYE_SIGNATURE,
        @Body requestBody: GreenEyeRequestBody,
    ): GreenEyeDto
}
