package com.flynnd273.activitytracker.workers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.flynnd273.activitytracker.utils.secondsUntil
import java.time.LocalTime
import java.util.concurrent.TimeUnit

fun updateResetTask(context: Context) {
    val resetJob = OneTimeWorkRequestBuilder<ResetProgressWorker>()
        .setInitialDelay(secondsUntil(LocalTime.MIDNIGHT), TimeUnit.SECONDS)
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "reset_task",
        ExistingWorkPolicy.REPLACE,
        resetJob
    )
}

fun queueReminder(time: LocalTime, context: Context) {
    val workRequest =
        OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(secondsUntil(time), TimeUnit.SECONDS)
            .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "remind_task",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}