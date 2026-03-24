package com.flynnd273.activitytracker

import kotlinx.serialization.Serializable

@Serializable
object AllActivities

@Serializable
data class ActivityDetails(val uid: Int)