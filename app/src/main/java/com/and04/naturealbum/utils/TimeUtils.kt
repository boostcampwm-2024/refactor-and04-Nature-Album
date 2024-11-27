package com.and04.naturealbum.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDateTime.toDate(): String {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.getDefault())
    return now.format(formatter)
}

fun String.toLocalDateTime(): LocalDateTime = LocalDateTime
    .parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    .atZone(ZoneId.of("UTC"))
    .withZoneSameInstant(ZoneId.systemDefault())
    .toLocalDateTime()

fun LocalDateTime.toDateTimeString(): String = format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
