package com.flynnd273.activitytracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DurationDialog(value: Int, onDismissRequest: (Int) -> Unit) {
    var seconds by remember { mutableStateOf(value) }
    Dialog(onDismissRequest = { onDismissRequest(seconds) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DurationPicker(
                        seconds = seconds,
                        onDurationChange = { seconds = it }
                    )
                }
                DialogButtons(onDismissRequest, value, seconds)
            }
        }
    }
}