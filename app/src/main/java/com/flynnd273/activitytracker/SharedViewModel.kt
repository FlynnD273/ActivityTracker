package com.flynnd273.activitytracker

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flynnd273.activitytracker.database.ActivityTask
import com.flynnd273.activitytracker.database.AppDatabase
import com.flynnd273.activitytracker.notifications.ActivityProgressService
import com.flynnd273.activitytracker.workers.queueReminder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime

private val Context.preferenceDatastore by preferencesDataStore("preferences")
private val REMINDER_TIME_KEY = intPreferencesKey("reminderTime")


@OptIn(FlowPreview::class)
class SharedViewModel(val appContext: Context) : ViewModel() {
    private val activityDao = AppDatabase.getInstance(appContext).activityDao()

    private val _activities = activityDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val activities: StateFlow<List<ActivityTask>> = _activities

    private val prefs = appContext.preferenceDatastore.data.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val reminderTime = appContext.preferenceDatastore.data
        .map { prefs ->
            LocalTime.ofSecondOfDay(
                prefs[REMINDER_TIME_KEY]?.toLong() ?: 0
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocalTime.MIDNIGHT
        )

    fun setReminderTime(time: LocalTime) {
        viewModelScope.launch {
            appContext.preferenceDatastore.edit {
                it[REMINDER_TIME_KEY] = time.toSecondOfDay()
            }
        }
    }

    init {
        viewModelScope.launch {
            reminderTime.debounce(500).collect {
                queueReminder(it, appContext)
            }
        }
    }

    fun updateActivity(activity: ActivityTask): ActivityTask {
        var newActivity = activity
        if (newActivity.name.isBlank()) {
            if (newActivity.uid != 0) {
                newActivity = newActivity.copy(name = newActivity.uid.toString())
            } else {
                newActivity = newActivity.copy(name = activities.value.size.toString())
            }
        }
        viewModelScope.launch {
            activityDao.update(newActivity)
        }
        if (newActivity.lastStart != null) {
            ActivityProgressService.StartService(appContext)
        }
        return newActivity
    }

    fun createNewActivity(activity: ActivityTask) {
        var newActivity = activity
        if (newActivity.name.isBlank()) {
            newActivity = newActivity.copy(name = (activities.value.size + 1).toString())
        }
        viewModelScope.launch {
            activityDao.insert(newActivity)
        }
    }

    fun deleteActivity(activity: ActivityTask) {
        viewModelScope.launch {
            activityDao.delete(activity)
        }
    }

    suspend fun loadActivity(uid: Int): ActivityTask {
        val activity = activityDao.get(uid)
        if (activity.isCompleted()) {
            return updateActivity(activity.toCompleted())
        }
        return activity
    }
}