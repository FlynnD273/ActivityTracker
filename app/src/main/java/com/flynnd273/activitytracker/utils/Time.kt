package com.flynnd273.activitytracker.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

fun Int.toTimestamp(): String {
    val totalSeconds = this
    val hours = totalSeconds / (60 * 60)
    val minutes = (totalSeconds % (60 * 60)) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

fun Int.toMinutesAndHours(): String {
    val totalSeconds = this
    val hours = totalSeconds / (60 * 60)
    val minutes = (totalSeconds % (60 * 60)) / 60
    return "%02d:%02d".format(hours, minutes)
}

fun secondsUntil(time: LocalTime): Long {
    val now = LocalDateTime.now()
    val nextTime = time.atDate(now.toLocalDate())
    val singleDaySeconds = Duration.ofDays(1).toSeconds()
    val delay = (Duration.between(now, nextTime).toSeconds() + singleDaySeconds) % singleDaySeconds
    if (delay == 0L) {
        return singleDaySeconds
    }
    return delay
}