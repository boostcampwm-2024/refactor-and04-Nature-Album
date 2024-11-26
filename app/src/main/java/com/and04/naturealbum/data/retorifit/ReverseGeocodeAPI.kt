package com.and04.naturealbum.data.retorifit

import com.and04.naturealbum.data.dto.ReverseGeocodeDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodeAPI {

    @GET("map-reversegeocode/v2/gc")
    suspend fun convertCoordsToAddress(
        @Query(value = "coords", encoded = true) coords: String,
        @Query("output") output: String? = "json",
        @Query("orders") orders: String? = "roadaddr",
    ): ReverseGeocodeDto
}
