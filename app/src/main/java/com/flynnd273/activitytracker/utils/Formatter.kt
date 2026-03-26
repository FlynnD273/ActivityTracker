package com.flynnd273.activitytracker.utils

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