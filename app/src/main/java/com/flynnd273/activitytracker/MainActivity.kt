package com.flynnd273.activitytracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
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
import com.flynnd273.activitytracker.workers.updateResetTask

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = SharedViewModel(applicationContext)
        updateResetTask(applicationContext)

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
                    PermissionsScreen({ permsGranted = checkPermissions() }, { checkBatterPerms() })
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val batteryGranted = checkBatterPerms()
        val notifsGranted = checkNotifPerms()
        return batteryGranted && notifsGranted
    }

    private fun checkNotifPerms(): Boolean {
        val notifsGranted = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        return notifsGranted
    }

    private fun checkBatterPerms(): Boolean {
        val pm = applicationContext.getSystemService(PowerManager::class.java) as PowerManager
        val batteryGranted = pm.isIgnoringBatteryOptimizations(applicationContext.packageName)
        return batteryGranted
    }

    private fun createNotificationChannels() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channels = listOf(
            NotificationChannel(
                getString(R.string.task_foreground_channel_id),
                getString(R.string.task_foreground_channel_name),
                importance
            ),
            NotificationChannel(
                getString(R.string.task_remind_channel_id),
                getString(R.string.task_remind_channel_name),
                importance
            ), NotificationChannel(
                getString(R.string.task_completed_channel_id),
                getString(R.string.task_completed_channel_name),
                importance
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 100, 100, 100)
            }, NotificationChannel(
                getString(R.string.task_progress_channel_id),
                getString(R.string.task_progress_channel_name),
                importance
            )
        )
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(channels)
    }
}