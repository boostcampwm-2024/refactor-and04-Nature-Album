package com.and04.naturealbum.data.retorifit

import com.and04.naturealbum.data.GreenEyeRequestBody
import com.and04.naturealbum.data.dto.GreenEyeDto
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
        @Path("domainId") domainId: String = "142",
        @Path("signature") signature: String = "78b7cf9742c0a587f1e4205b8395605e0b08e220ab2c07e59168ca6b65dcb700",
        @Body requestBody: GreenEyeRequestBody,
    ): GreenEyeDto
}
