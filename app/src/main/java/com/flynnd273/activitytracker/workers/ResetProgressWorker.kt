package com.flynnd273.activitytracker.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.flynnd273.activitytracker.SharedViewModel

class ResetProgressWorker(
    val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val viewModel = SharedViewModel(context)
        for (activity in viewModel.activities.value) {
            viewModel.updateActivity(activity.toReset())
        }
        updateResetTask(context)
        return Result.success()
    }
}