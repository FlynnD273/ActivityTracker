package com.flynnd273.activitytracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.flynnd273.activitytracker.database.ActivityTask
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    activities: List<ActivityTask>,
    navigateToActivity: (Int) -> Unit
) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(500)
        }
    }
    Column {
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
    }
}