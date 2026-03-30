package com.flynnd273.activitytracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.flynnd273.activitytracker.ui.screens.PermissionsScreen
import com.flynnd273.activitytracker.ui.theme.ActivityTrackerTheme
import com.flynnd273.activitytracker.workers.queueReminderTask
import com.flynnd273.activitytracker.workers.queueResetTask

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = SharedViewModel(applicationContext)
        queueResetTask(applicationContext)
        queueReminderTask(viewModel.reminderTime.value, applicationContext)

        createNotificationChannels()

        setContent {
            var permsGranted by remember { mutableStateOf(true) }
            var initPerms by remember { mutableStateOf(true) }

            ActivityTrackerTheme {
                if (permsGranted) {
                    App(viewModel)
                }
                if (!permsGranted || initPerms) {
                    PermissionsScreen({ permsGranted = false }, {
                        permsGranted = true
                        initPerms = false
                    })
                }
            }
        }
    }

    private fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                getString(R.string.task_foreground_channel_id),
                getString(R.string.task_foreground_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            NotificationChannel(
                getString(R.string.task_remind_channel_id),
                getString(R.string.task_remind_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ), NotificationChannel(
                getString(R.string.task_completed_channel_id),
                getString(R.string.task_completed_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 100, 100, 100)
            }, NotificationChannel(
                getString(R.string.task_progress_channel_id),
                getString(R.string.task_progress_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableVibration(false)
            }
        )
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(channels)
    }
}