package com.flynnd273.activitytracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.flynnd273.activitytracker.ui.theme.ActivityTrackerTheme

@Composable
fun App(viewModel: SharedViewModel) {
    val activities by viewModel.activities.collectAsState()
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
                        HorizontalDivider()
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(it.name, fontSize = 4.em)
                            LinearProgressIndicator(
                                progress = { it.progress.toFloat() / it.goal.coerceAtLeast(1) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            )
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
