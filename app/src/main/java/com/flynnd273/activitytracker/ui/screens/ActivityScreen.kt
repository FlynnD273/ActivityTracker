package com.flynnd273.activitytracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.flynnd273.activitytracker.SharedViewModel
import com.flynnd273.activitytracker.database.ActivityTask
import com.flynnd273.activitytracker.utils.toMinutesAndHours
import com.flynnd273.activitytracker.utils.toTimestamp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

@OptIn(FlowPreview::class)
@Composable
fun ActivityScreen(viewModel: SharedViewModel, uid: Int) {
    var showEditMenu by remember { mutableStateOf(false) }
    var mutableActivity by remember { mutableStateOf<ActivityTask?>(null) }
    LaunchedEffect(uid) {
        mutableActivity = viewModel.loadActivity(uid)
    }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(500)
        }
    }

    mutableActivity?.let { activity ->
        Scaffold(floatingActionButton = {
            FloatingActionButton(onClick = { showEditMenu = true }) {
                Icon(imageVector = Icons.Default.Edit, "Edit")
            }
        }) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text(
                        activity.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.em,
                        textAlign = TextAlign.Center,
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
                                                    ).seconds.toInt())
                                        )
                                    )
                                }
                            }),
                        contentAlignment = Alignment.Center
                    ) {
                        val density = LocalDensity.current
                        val progressFontSize = with(density) {
                            (maxHeight * 0.15f).toSp()
                        }
                        val goalFontSize = progressFontSize * 0.75f

                        val progressValue by remember(activity) {
                            derivedStateOf {
                                val baseProgress = activity.progress
                                if (activity.lastStart != null) {
                                    val elapsed = Duration.between(activity.lastStart, now)
                                    baseProgress + elapsed.seconds.toInt()
                                } else {
                                    baseProgress
                                }
                            }
                        }
                        CircularProgressIndicator(
                            progress = { progressValue.toFloat() / activity.goal.coerceAtLeast(1) },
                            modifier = Modifier
                                .fillMaxSize(),
                            color = activity.color ?: ProgressIndicatorDefaults.linearColor,
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                progressValue.toTimestamp(),
                                fontSize = progressFontSize,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                activity.goal.toMinutesAndHours(),
                                fontSize = goalFontSize,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            if (showEditMenu) {
                EditActivityDialog(activity, {
                    mutableActivity = viewModel.updateActivity(it)
                    showEditMenu = false
                })
            }
        }
    }
}

