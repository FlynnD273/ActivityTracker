package com.flynnd273.activitytracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.flynnd273.activitytracker.database.ActivitiesTable
import com.flynnd273.activitytracker.database.ActivityTask
import com.flynnd273.activitytracker.database.initDb
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = getApplication<Application>()

    private val _activities = MutableStateFlow(emptyList<ActivityTask>())
    val activities = _activities.asStateFlow()

    init {
        val dbFile = appContext.getDatabasePath("activities.db")
        Database.connect("jdbc:sqlite:file:${dbFile.absolutePath}", "org.sqlite.Driver")
        initDb()
        refreshActivities()
        if (activities.value.isEmpty()) {
            transaction {
                ActivityTask.new {
                    name = "My first activity"
                    goalTime = 30
                    accumulatedTime = 10
                }
            }
        }
    }

    fun refreshActivities() {
        transaction {
            _activities.value = ActivityTask.all().sortedBy { ActivitiesTable.name }.toList()
        }
    }
}