package com.flynnd273.activitytracker.ui.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flynnd273.activitytracker.database.ActivityTask
import com.flynnd273.activitytracker.ui.EditActivityDialog
import com.flynnd273.activitytracker.utils.toMinutesAndHours
import com.flynnd273.activitytracker.utils.toTimestamp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

@OptIn(FlowPreview::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    loadActivity: suspend (Int) -> ActivityTask,
    updateActivity: (ActivityTask) -> ActivityTask,
    deleteActivity: (ActivityTask) -> Unit,
    uid: Int,
    navigateToHome: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope
) {
    var showEditMenu by remember { mutableStateOf(false) }
    var showDeleteMenu by remember { mutableStateOf(false) }
    var mutableActivity by remember { mutableStateOf<ActivityTask?>(null) }
    LaunchedEffect(uid) {
        mutableActivity = loadActivity(uid)
    }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            mutableActivity?.let { activity ->
                if (activity.isCompleted(now)) {
                    mutableActivity = activity.toCompleted()
                }
            }
            delay(100)
        }
    }

    var floatMenuExpanded by rememberSaveable { mutableStateOf(false) }
    mutableActivity?.let { activity ->
        Scaffold(floatingActionButton = {
            with(sharedTransitionScope) {
                FloatingActionButtonMenu(
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(key = "fab"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.None),
                    ),
                    expanded = floatMenuExpanded, button = {
                        ToggleFloatingActionButton(
                            checked = floatMenuExpanded,
                            onCheckedChange = { floatMenuExpanded = it }) {
                            Icon(imageVector = Icons.Default.Settings, "Settings")
                        }
                    }) {
                    FloatingActionButtonMenuItem(onClick = {
                        showDeleteMenu = true
                        floatMenuExpanded = false
                    }, icon = {
                        Icon(imageVector = Icons.Default.Delete, "Delete")
                    }, text = { Text("Delete") })
                    if (activity.progress > 0 || activity.lastStart != null) {
                        FloatingActionButtonMenuItem(onClick = {
                            mutableActivity = updateActivity(activity.toReset())
                            floatMenuExpanded = false
                        }, icon = {
                            Icon(Icons.Default.Refresh, "Reset")
                        }, text = { Text("Reset") })
                    }
                    FloatingActionButtonMenuItem(onClick = {
                        showEditMenu = true
                        floatMenuExpanded = false
                    }, icon = {
                        Icon(imageVector = Icons.Default.Edit, "Edit")
                    }, text = { Text("Edit") })
                }
            }
        }) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        activity.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                    )
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(12.dp)
                            .clip(CircleShape)
                            .clickable(enabled = activity.progress < activity.goal, onClick = {
                                if (activity.lastStart == null) {
                                    mutableActivity = updateActivity(activity.copy(lastStart = now))
                                } else {
                                    mutableActivity = updateActivity(
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
                                    baseProgress + elapsed.toMillis() / 1000f
                                } else {
                                    baseProgress
                                }
                            }
                        }
                        CircularWavyProgressIndicator(
                            progress = { progressValue.toFloat() / activity.goal.coerceAtLeast(1) },
                            modifier = Modifier
                                .fillMaxSize(),
                            color = activity.color ?: ProgressIndicatorDefaults.linearColor,
                            amplitude = { if (activity.lastStart == null) 0f else 1f },
                            wavelength = 50.dp,
                            waveSpeed = 5.dp,
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                progressValue.toInt().toTimestamp(),
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
                EditActivityDialog("Edit Activity", activity, {
                    if (it != null) {
                        mutableActivity = updateActivity(it)
                    }
                    showEditMenu = false
                })
            }
            if (showDeleteMenu) {
                BasicAlertDialog(onDismissRequest = { showDeleteMenu = false }) {
                    Surface(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = AlertDialogDefaults.TonalElevation,
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text =
                                    "Are you sure you want to delete this activity?"
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                TextButton(
                                    onClick = {
                                        showDeleteMenu = false
                                    },
                                ) {
                                    Text("Cancel")
                                }
                                TextButton(
                                    onClick = {
                                        showDeleteMenu = false
                                        deleteActivity(activity)
                                        navigateToHome()
                                    },
                                ) {
                                    Text("Confirm")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

