package com.flynnd273.activitytracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.flynnd273.activitytracker.SharedViewModel
import com.flynnd273.activitytracker.database.ActivityTask
import com.flynnd273.activitytracker.utils.toTimestamp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import java.time.Duration
import java.time.LocalDateTime

@OptIn(FlowPreview::class)
@Composable
fun ActivityScreen(viewModel: SharedViewModel, uid: Int) {
    var mutableActivity by remember { mutableStateOf<ActivityTask?>(null) }
    LaunchedEffect(uid) {
        mutableActivity = viewModel.loadActivity(uid)
    }

    LaunchedEffect(mutableActivity) {
        snapshotFlow { mutableActivity }
            .filterNotNull()
            .debounce(500)
            .collect { viewModel.updateActivity(it) }
    }

    mutableActivity?.let { activity ->
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true,
                textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 4.em, textAlign = TextAlign.Center),
                value = activity.name,
                onValueChange = { newName ->
                    if (newName.isNotBlank()) {
                        mutableActivity = viewModel.updateActivity(activity.copy(name = newName))
                    }
                }
            )
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                val density = LocalDensity.current
                val fontSize = with(density) {
                    (maxHeight * 0.2f).toSp()
                }

                val progressValue = remember(activity) {
                    derivedStateOf {
                        val baseProgress = activity.progress
                        if (activity.lastStart != null) {
                            val elapsed =
                                (Duration.between(activity.lastStart, LocalDateTime.now()).toMillis() / 1000)
                            baseProgress + elapsed
                        } else {
                            baseProgress
                        }
                    }
                }
                CircularProgressIndicator(
                    progress = { progressValue.value.toFloat() / activity.goal.coerceAtLeast(1) },
                    modifier = Modifier
                        .fillMaxSize(),
                    color = activity.color ?: ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
                Text(progressValue.value.toTimestamp(), fontSize = fontSize)
            }
        }
    }
}