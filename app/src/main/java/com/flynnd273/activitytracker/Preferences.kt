package com.flynnd273.activitytracker

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

class Preferences(context: Context) {
    private val Context.preferenceDatastore by preferencesDataStore("preferences")
}