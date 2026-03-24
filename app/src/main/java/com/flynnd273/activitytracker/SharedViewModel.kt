package com.flynnd273.activitytracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.flynnd273.activitytracker.database.ActivityTask
import com.flynnd273.activitytracker.database.AppDatabase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = getApplication<Application>()

    val db = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "app-database"
    ).build()
    private val activityDao = db.activityDao()

    private val _activities = activityDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val activities: StateFlow<List<ActivityTask>> = _activities

    init {
        viewModelScope.launch {
            activityDao.getAll().collectLatest {
                if (it.isEmpty()) {
                    activityDao.insert(
                        ActivityTask(
                            name = "My first activity",
                            goal = 30,
                            progress = 10,
                            color = null,
                        )
                    )
                }
            }
        }
    }

    fun updateActivity(activity: ActivityTask): ActivityTask {
        viewModelScope.launch {
            activityDao.update(activity)
        }
        return activity
    }

    suspend fun loadActivity(uid: Int): ActivityTask {
        return activityDao.get(uid)
    }
}