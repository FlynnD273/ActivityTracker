package com.flynnd273.activitytracker

import kotlinx.serialization.Serializable

@Serializable
object HomeScreenRoute

@Serializable
data class ActivityScreenRoute(val uid: Int)