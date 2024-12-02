package com.and04.naturealbum.data.mapper

import com.and04.naturealbum.data.dto.ReverseGeocodeDto

object ReverseGeocodeMapper {
    private const val SEPARATOR = "%2C"
    fun mapCoordsToAddress(
        reverseGeocodeDto: ReverseGeocodeDto,
        latitude: Double,
        longitude: Double,
    ): String {
        if (reverseGeocodeDto.results.isNullOrEmpty()) {
            return mapCoordsToRequestCoords(latitude, longitude)
        }
        val region = reverseGeocodeDto.results[0].region
        val address = buildString {
            append("${region?.area1?.name} ")
            append("${region?.area2?.name} ")
            append("${region?.area3?.name} ")
            append(region?.area4?.name)
        }
        return address
    }

    fun mapCoordsToRequestCoords(latitude: Double, longitude: Double): String {
        return "${longitude}$SEPARATOR${latitude}"
    }
}
