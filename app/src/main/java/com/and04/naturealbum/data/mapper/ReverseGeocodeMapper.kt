package com.and04.naturealbum.data.mapper

import com.and04.naturealbum.data.dto.ReverseGeocodeDto

object ReverseGeocodeMapper {
    private const val SEPARATOR = ", "
    fun mapCoordsToAddress(
        reverseGeocodeDto: ReverseGeocodeDto,
        latitude: Double,
        longitude: Double,
    ): String {
        if (reverseGeocodeDto.results.isNullOrEmpty()) {
            return mapCoordsToRequestCoords(latitude, longitude)
        }
        val region = reverseGeocodeDto.results[0].region
        val land = reverseGeocodeDto.results[0].land
        val address = buildString {
            append("${region?.area1?.name} ")
            append("${region?.area2?.name} ")
            append("${land?.name} ")
            append("${land?.number1}")
        }
        return address
    }

    fun mapCoordsToRequestCoords(latitude: Double, longitude: Double): String {
        return "${latitude}$SEPARATOR${longitude}"
    }
}
