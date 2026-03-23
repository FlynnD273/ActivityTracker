package com.flynnd273.activitytracker

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.flynnd273.activitytracker.ui.theme.ActivityTrackerTheme
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.min

@Composable
fun App(viewModel: SharedViewModel) {
    val activities by viewModel.activities.collectAsState()
    val navController = rememberNavController()
    NavHost(navController, startDestination = HomeScreen) {
        ActivityTrackerTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    Text(
                        "My Activities",
                        fontSize = 12.em,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                    LazyColumn {
                        items(activities) {
                            val animatedProgress by if (it.lastStart != null) {
                                val infiniteTransition = rememberInfiniteTransition()
                                infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Restart
                                    )
                                )
                            } else {
                                remember { mutableStateOf(0f) }
                            }

                            val progressValue = remember(it) {
                                derivedStateOf {
                                    val baseProgress = it.progress.toFloat() / it.goal.coerceAtLeast(1)
                                    if (it.lastStart != null) {
                                        val elapsed = Duration.between(it.lastStart, LocalDateTime.now()).toMillis()
                                        // You can scale elapsed to your desired unit. Here 1 sec = 0.001 progress
                                        min(1f, baseProgress + elapsed / 100_000f)
                                    } else {
                                        baseProgress
                                    }
                                }
                            }
                            HorizontalDivider()
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(it.name, fontSize = 4.em)
                                LinearProgressIndicator(
                                    progress = { progressValue.value },
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
                }
            }
        }
    }
}