package com.flynnd273.activitytracker.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.flynnd273.activitytracker.MainActivity
import com.flynnd273.activitytracker.R
import com.flynnd273.activitytracker.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderWorker(
    val context: Context,
    val params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val viewModel = SharedViewModel(context)
        CoroutineScope(params.workerContext).launch {
            for (activity in viewModel.activities.drop(1).first()
                .filter { it.lastStart == null && it.progress < it.goal }) {
                val deepLinkIntent = Intent(
                    Intent.ACTION_VIEW,
                    "${context.getString(R.string.app_uri)}activity/${activity.uid}".toUri(),
                    context,
                    MainActivity::class.java
                )
                val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                    addNextIntentWithParentStack(deepLinkIntent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                }

                val notification =
                    NotificationCompat.Builder(context, context.getString(R.string.task_remind_channel_id))
                        .setContentTitle(context.getString(R.string.task_remind_notif_title).format(activity.name))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .let {
                            if (activity.color != null) {
                                it.setColor(activity.color.toArgb())
                            } else {
                                it
                            }
                        }
                        .setAutoCancel(true).apply {
                            setContentIntent(deepLinkPendingIntent)
                        }

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(activity.uid, notification.build())
            }
        }
        queueReminder(viewModel.reminderTime.value, context)
        return Result.success()
    }
}