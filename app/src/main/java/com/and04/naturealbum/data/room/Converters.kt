package com.and04.naturealbum.data.room

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun localDateTimeToString(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun stringToLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { timeString ->
            LocalDateTime
                .parse(timeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        }
    }
}
