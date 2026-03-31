package com.flynnd273.activitytracker.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.widget.RemoteViews
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.flynnd273.activitytracker.MainActivity
import com.flynnd273.activitytracker.R
import com.flynnd273.activitytracker.database.ActivityDao
import com.flynnd273.activitytracker.database.ActivityTask
import com.flynnd273.activitytracker.database.AppDatabase
import kotlinx.coroutines.*
import java.time.LocalDateTime


class ActivityProgressService : Service() {
    companion object {
        var isRunning = false

        fun StartService(context: Context) {
            if (!isRunning) {
                val intent = Intent(context, ActivityProgressService::class.java)
                ContextCompat.startForegroundService(context, intent)
            }
        }
    }

    private lateinit var notificationManager: NotificationManager
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var activityDao: ActivityDao

    private lateinit var colorScheme: ColorScheme

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        activityDao = AppDatabase.getInstance(applicationContext).activityDao()

        val isDarkTheme =
            (applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        colorScheme =
            if (isDarkTheme) dynamicDarkColorScheme(applicationContext) else dynamicLightColorScheme(applicationContext)

        val notification = NotificationCompat.Builder(this, getString(R.string.task_foreground_channel_id))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.task_foreground_channel_name))
            .build()
        startForeground(1000, notification)
    }

    private var allTasks: List<ActivityTask> = emptyList()
    private var progressedTasks: MutableSet<Int> = mutableSetOf()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch(Dispatchers.IO) {
            activityDao.getAll().collect {
                allTasks = it
            }
        }
        scope.launch {
            while (isActive) {
                updateProgressNotifications(allTasks)
                delay(1000)
            }
        }
        return START_STICKY
    }

    private suspend fun updateProgressNotifications(tasks: List<ActivityTask>) {
        val now = LocalDateTime.now()
        for (activity in tasks) {
            if (activity.lastStart == null) {
                if (progressedTasks.contains(activity.uid)) {
                    notificationManager.cancel(activity.uid)
                }
                continue
            }
            val notification = buildNotification(activity, now)
            notificationManager.notify(activity.uid, notification)
            progressedTasks.add(activity.uid)

            if (activity.isCompleted(now)) {
                progressedTasks.remove(activity.uid)
                activityDao.update(activity.toCompleted())
                notificationManager.notify(
                    activity.uid,
                    buildDoneNotification(activity)
                )
            }
        }

        if (tasks.all { it.lastStart == null }) {
            stopSelf()
        }
    }

    private fun buildNotification(activity: ActivityTask, now: LocalDateTime): Notification {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "${getString(R.string.app_uri)}activity/${activity.uid}".toUri(),
            this,
            MainActivity::class.java
        )
        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(activity.uid, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val remoteViews = RemoteViews(packageName, R.layout.progress_notif)
        remoteViews.setTextViewText(R.id.title, getString(R.string.task_progress_notif_title).format(activity.name))
        remoteViews.setTextColor(R.id.title, colorScheme.onBackground.toArgb())
        remoteViews.setProgressBar(
            R.id.progress,
            activity.goal,
            activity.realProgress(now),
            false
        )
        remoteViews.setColorStateList(
            R.id.progress, "setProgressTintList",
            ColorStateList.valueOf((activity.color ?: colorScheme.primary).toArgb())
        )


        return NotificationCompat.Builder(this, getString(R.string.task_progress_channel_id))
            .setContentTitle(getString(R.string.task_progress_notif_title).format(activity.name))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOnlyAlertOnce(true)
//            .setStyle(
//                NotificationCompat.ProgressStyle()
//                    .setProgress(activity.realProgress(now))
//                    .addProgressSegment(NotificationCompat.ProgressStyle.Segment(activity.goal).let {
//                        if (activity.color != null) {
//                            it.setColor(activity.color.toArgb())
//                        } else {
//                            it.setColor(colorScheme.primary.toArgb())
//                        }
//                    })
//            )
//            .setProgress(activity.goal, activity.realProgress(now), false)
//            .setRequestPromotedOngoing(true)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setOngoing(true)
            .setContentIntent(deepLinkPendingIntent)
            .build()
    }

    private fun buildDoneNotification(activity: ActivityTask): Notification {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "${getString(R.string.app_uri)}activity/${activity.uid}".toUri(),
            this,
            MainActivity::class.java
        )
        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(activity.uid, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        return NotificationCompat.Builder(this, getString(R.string.task_completed_channel_id))
            .setContentTitle(getString(R.string.task_completed_notif_title).format(activity.name))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(deepLinkPendingIntent)
            .build()
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        isRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        scope.cancel()
        super.onDestroy()
    }
}