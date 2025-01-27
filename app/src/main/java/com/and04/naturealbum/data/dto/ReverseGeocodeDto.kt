package com.and04.naturealbum.data.dto

data class ReverseGeocodeDto(
    val status: Status?,
    val results: List<Result>?,
)

data class Status(
    val code: Int?,
    val name: String?,
    val message: String?,
)

data class Result(
    val name: String?,
    val code: Code?,
    val region: Region?,
    val land: Land?,
)

data class Code(
    val id: String?,
    val type: String?,
    val mappingId: String?,
)

data class Region(
    val area0: Area?,
    val area1: Area?,
    val area2: Area?,
    val area3: Area?,
    val area4: Area?,
)

data class Area(
    val name: String?,
    val coords: Coords?,
    val alias: String?, // name이 전라남도였다면 alias 는 전남으로 온다
)

data class Coords(
    val center: Center?,
)

data class Center(
    val crs: String?,
    val x: Double?,
    val y: Double?,
)

data class Land(
    val type: String?,
    val number1: String?,
    val number2: String?,
    val addition0: Addition?,
    val addition1: Addition?,
    val addition2: Addition?,
    val addition3: Addition?,
    val addition4: Addition?,
    val name: String?,
    val coords: Coords?,
)

data class Addition(
    val type: String?,
    val value: String?,
)
