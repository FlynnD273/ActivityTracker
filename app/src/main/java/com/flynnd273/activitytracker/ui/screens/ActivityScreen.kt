package com.flynnd273.activitytracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
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
import kotlinx.coroutines.delay
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

    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(500)
        }
    }

    mutableActivity?.let { activity ->
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .onFocusChanged() {
                        if (!it.isFocused) {
                            if (activity.name.isBlank()) {
                                mutableActivity =
                                    viewModel.updateActivity(activity.copy(name = activity.uid.toString()))
                            }
                        }
                    },
                singleLine = true,
                textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 4.em, textAlign = TextAlign.Center),
                value = activity.name,
                onValueChange = { newName ->
                    if (newName.isBlank()) {
                        viewModel.updateActivity(activity.copy(name = activity.uid.toString()))
                    } else {
                        mutableActivity = viewModel.updateActivity(activity.copy(name = newName))
                    }
                }
            )
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(12.dp)
                    .clickable(onClick = {
                        if (activity.lastStart == null) {
                            mutableActivity = viewModel.updateActivity(activity.copy(lastStart = now))
                        } else {
                            mutableActivity = viewModel.updateActivity(
                                activity.copy(
                                    lastStart = null,
                                    progress = activity.progress +
                                            (Duration.between(
                                                activity.lastStart,
                                                LocalDateTime.now()
                                            ).seconds)
                                )
                            )
                        }
                    }),
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
                            val elapsed = Duration.between(activity.lastStart, now)
                            baseProgress + elapsed.seconds
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
                )
                Text(progressValue.value.toTimestamp(), fontSize = fontSize)
            }
            Button(onClick = {
                mutableActivity = viewModel.updateActivity(activity.copy(lastStart = null, progress = 0))
            }) {
                Text("Reset Progress")
            }
        }
    }
}