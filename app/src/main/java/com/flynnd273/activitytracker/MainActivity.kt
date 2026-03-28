package com.flynnd273.activitytracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
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
            var permsGranted by remember {
                mutableStateOf(
                    checkPermissions()
                )
            }

            ActivityTrackerTheme {
                if (permsGranted) {
                    App(viewModel)
                } else {
                    PermissionsScreen({ permsGranted = checkPermissions() }, { checkBatteryPerms() })
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val batteryGranted = checkBatteryPerms()
        val notifsGranted = checkNotifPerms()
        return batteryGranted && notifsGranted
    }

    private fun checkNotifPerms(): Boolean {
        val notifsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
        return notifsGranted
    }

    private fun checkBatteryPerms(): Boolean {
        val pm = applicationContext.getSystemService(PowerManager::class.java) as PowerManager
        val batteryGranted = pm.isIgnoringBatteryOptimizations(applicationContext.packageName)
        return batteryGranted
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