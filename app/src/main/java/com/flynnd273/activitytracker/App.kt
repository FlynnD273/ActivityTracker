package com.flynnd273.activitytracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.em
import com.flynnd273.activitytracker.ui.theme.ActivityTrackerTheme

@Composable
fun App(viewModel: SharedViewModel) {
    val activities by viewModel.activities.collectAsState()
    ActivityTrackerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Text("My Activities", fontSize = 12.em)
                LazyColumn {
                    items(activities) {
                        HorizontalDivider()
                        Column {
                            Text(it.name, fontSize = TextAutoSizeDefaults.MaxFontSize)
                        }
                    }
                }
            }
        }
    }
}
