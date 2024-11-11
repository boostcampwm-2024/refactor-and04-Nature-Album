package com.and04.naturealbum.data.room

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class Converters {
    @TypeConverter
    fun localDateTimeToString(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    // UTC -> Local 매핑되는지 확인 필요
    @TypeConverter
    fun stringToLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { timeString ->
            OffsetDateTime.parse(timeString).atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        }
    }
}
