package com.flynnd273.activitytracker.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flynnd273.activitytracker.SharedViewModel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.firstOrNull

class ResetProgressWorker(
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("RESET", "Resetting all activities")
        val viewModel = SharedViewModel(context)
        for (activity in (viewModel.activities.drop(1).firstOrNull() ?: emptyList())) {
            viewModel.updateActivity(activity.toReset())
        }
        queueResetTask(context)
        return Result.success()
    }
}