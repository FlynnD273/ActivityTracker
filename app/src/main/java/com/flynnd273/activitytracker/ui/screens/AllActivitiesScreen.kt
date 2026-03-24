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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.flynnd273.activitytracker.database.ActivityTask
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.min

@Composable
fun AllActivitiesScreen(
    activities: List<ActivityTask>,
    navigateToActivity: (Int) -> Unit
) {
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
                val progressValue = remember(it) {
                    derivedStateOf {
                        val baseProgress = it.progress.toFloat() / it.goal.coerceAtLeast(1)
                        if (it.lastStart != null) {
                            val elapsed = Duration.between(it.lastStart, LocalDateTime.now()).toMillis()
                            min(1f, baseProgress + elapsed / 100_000f)
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