package com.flynnd273.activitytracker

import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Serializable
data class ActivityScreen(val uid: Int)