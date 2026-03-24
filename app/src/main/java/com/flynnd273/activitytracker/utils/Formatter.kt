package com.flynnd273.activitytracker.utils

fun Long.toTimestamp(): String {
    val totalSeconds = this
    val hours = totalSeconds / (60 * 60)
    val minutes = (totalSeconds % (60 * 60)) / 60
    val seconds = totalSeconds % 60
    if (hours > 0) {
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
    return "%02d:%02d".format(minutes, seconds)
}

fun Long.toMinutesAndHours(): String {
    val totalSeconds = this
    val hours = totalSeconds / (60 * 60)
    val minutes = (totalSeconds % (60 * 60)) / 60
    if (hours > 0) {
        return "%02d:%02d".format(hours, minutes)
    }
    return "%02d".format(minutes)
}