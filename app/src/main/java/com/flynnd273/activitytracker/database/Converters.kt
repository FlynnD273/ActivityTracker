package com.flynnd273.activitytracker.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): LocalDateTime {
        return value.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }
    }

    @TypeConverter
    fun localDateTimeToTimestamp(date: LocalDateTime): Long {
        return date.atZone(ZoneId.systemDefault())
            .toInstant()
            .epochSecond
    }
}