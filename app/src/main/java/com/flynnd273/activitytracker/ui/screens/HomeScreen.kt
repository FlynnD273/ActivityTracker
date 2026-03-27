package com.flynnd273.activitytracker.ui.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.flynnd273.activitytracker.database.ActivityTask
import com.flynnd273.activitytracker.ui.EditActivityDialog
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    activities: List<ActivityTask>,
    createNewActivity: (ActivityTask) -> Unit,
    navigateToActivity: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
) {
    var addingNewActivity by remember { mutableStateOf(false) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(500)
        }
    }
    Scaffold(floatingActionButton = {
        with(sharedTransitionScope) {
            FloatingActionButton(
                modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(key = "fab"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.None),
                    ),
                onClick = { addingNewActivity = true }) {
                Icon(Icons.Default.Add, "Create new")
            }
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                "My Activities",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
            if (activities.isNotEmpty()) {
                LazyColumn {
                    items(activities) {
                        val progressValue by remember(it) {
                            derivedStateOf {
                                val baseProgress = it.progress
                                if (it.lastStart != null) {
                                    val elapsed = Duration.between(it.lastStart, now)
                                    baseProgress + elapsed.seconds.toInt()
                                } else {
                                    baseProgress
                                }
                            }
                        }
                        HorizontalDivider()
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable(onClick = { navigateToActivity(it.uid) })
                        ) {
                            Text(it.name, fontSize = 4.em)
                            LinearProgressIndicator(
                                progress = { progressValue.toFloat() / it.goal.coerceAtLeast(1) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                color = it.color ?: ProgressIndicatorDefaults.linearColor,
                                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                            )
                        }
                    }
                }
                HorizontalDivider()
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No activities created yet.",
                        modifier = Modifier.graphicsLayer(alpha = 0.5f),
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
        if (addingNewActivity) {
            EditActivityDialog("Create Activity", ActivityTask(name = "New Activity", goal = 30 * 60), {
                if (it != null) {
                    createNewActivity(it)
                }
                addingNewActivity = false
            })
        }
    }
}