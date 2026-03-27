package com.flynnd273.activitytracker.database

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.room.TypeConverter
import com.flynnd273.activitytracker.utils.Day
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

    @TypeConverter
    fun colorToLong(color: Color): Long {
        return color.toColorLong()
    }

    @TypeConverter
    fun longToColor(color: Long): Color {
        return Color.fromColorLong(color)
    }

    @TypeConverter
    fun daysToString(days: List<Day>): String {
        return days.map { it.name }.joinToString(",")
    }

    @TypeConverter
    fun stringsToDay(days: String): List<Day> {
        return days.split(",").map { Day.valueOf(it) }
    }
}