package com.and04.naturealbum.data.mapper

import com.and04.naturealbum.data.dto.ReverseGeocodeDto
import com.and04.naturealbum.data.repository.RetrofitRepositoryImpl.Companion.EMPTY_ADDRESS

object ReverseGeocodeMapper {
    private const val SEPARATOR = "%2C"
    fun mapCoordsToAddress(
        reverseGeocodeDto: ReverseGeocodeDto,
    ): String {
        if (reverseGeocodeDto.results.isNullOrEmpty()) {
            return EMPTY_ADDRESS
        }
        val region = reverseGeocodeDto.results.lastOrNull()?.region
        val address =
            listOfNotNull(
                region?.area1?.name,
                region?.area2?.name,
                region?.area3?.name,
                region?.area4?.name
            ).joinToString(" ")
        return address
    }

    fun mapCoordsToRequestCoords(latitude: Double, longitude: Double): String {
        return "${longitude}$SEPARATOR${latitude}"
    }
}
