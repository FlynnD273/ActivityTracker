package com.flynnd273.activitytracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import com.flynnd273.activitytracker.database.ActivityTask
import com.flynnd273.activitytracker.utils.toMinutesAndHours
import kotlinx.coroutines.FlowPreview


@OptIn(FlowPreview::class)
@Composable
fun EditActivityDialog(label: String, value: ActivityTask, onDialogClose: (ActivityTask?) -> Unit) {
    var activity by remember { mutableStateOf(value) }
    var editingGoal by remember { mutableStateOf(false) }
    var editingColor by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = { onDialogClose(null) }) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Text(
                label,
                fontSize = 8.em,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
            HorizontalDivider(modifier = Modifier.padding(4.dp))
            ColumnPair(
                { Text("Title:") },
                {
                    TextField(
                        value = activity.name,
                        textStyle = TextStyle(textAlign = TextAlign.Center),
                        onValueChange = { activity = activity.copy(name = it) })
                },
                { Text("Goal:") },
                {
                    Text(
                        activity.goal.toMinutesAndHours(),
                        modifier = Modifier.clickable(onClick = { editingGoal = true })
                    )
                },
                { Text("Color:") },
                {
                    Box(
                        modifier = Modifier
                            .background(activity.color ?: MaterialTheme.colorScheme.primary)
                            .size(24.dp)
                            .clickable(onClick = { editingColor = true }),
                    )
                },
                {
                    IconButton(onClick = {
                        onDialogClose(null)
                    }) {
                        Icon(Icons.Default.Close, "Cancel")
                    }
                },
                {
                    IconButton(onClick = {
                        onDialogClose(activity)
                    }) {
                        Icon(Icons.Default.Check, "Save")
                    }
                })
        }
        if (editingGoal) {
            DurationDialog(value = activity.goal) {
                activity = activity.copy(goal = it)
                editingGoal = false
            }
        }
        if (editingColor) {
            ColorPickerDialog(activity.color) {
                activity = activity.copy(color = it)
                editingColor = false
            }
        }
    }
}

@Composable
private fun ColumnPair(vararg items: (@Composable () -> Unit)?) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
    ) {
        items.toList().chunked(2).forEachIndexed { index, pair ->
            if (index + 1 == items.size / 2) {
                Spacer(modifier = Modifier.height(64.dp))
            }
            if (pair[1] == null && pair[0] != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    pair[0]!!()
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pair.forEach { item ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f),
                        ) {
                            if (item != null) {
                                item()
                            }
                        }
                    }
                }
            }
        }
    }
}