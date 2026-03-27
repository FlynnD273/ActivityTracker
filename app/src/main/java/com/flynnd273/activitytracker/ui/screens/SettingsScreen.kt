package com.flynnd273.activitytracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    reminderTimeState: StateFlow<LocalTime>,
    setReminderTime: (LocalTime) -> Unit,
    resetAll: () -> Unit,
    hasProgress: Boolean,
) {
    val reminderTime by reminderTimeState.collectAsState()
    var showReminderTimeDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = reminderTime.hour,
        initialMinute = reminderTime.minute,
    )

    LaunchedEffect(0) {
        reminderTimeState.collect {
            timePickerState.hour = it.hour
            timePickerState.minute = it.minute
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("Reminder time:", style = MaterialTheme.typography.bodyLarge)
            Button(onClick = { showReminderTimeDialog = true }) {
                Text(
                    if (timePickerState.is24hour) {
                        reminderTime.toString()
                    } else {
                        if (reminderTime.hour < 12) {
                            if (reminderTime.hour == 0) {
                                "${reminderTime + Duration.ofHours(12)} AM"
                            } else {
                                "$reminderTime AM"
                            }
                        } else {
                            if (reminderTime.hour == 12) {
                                "$reminderTime PM"
                            } else {
                                "${reminderTime - Duration.ofHours(12)} PM"
                            }
                        }
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Button(onClick = resetAll, enabled = hasProgress) {
            Text(
                "Reset all progress",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    if (showReminderTimeDialog) {
        TimePickerDialog(
            onDismissRequest = {
                showReminderTimeDialog = false
                timePickerState.hourInput = reminderTime.hour
                timePickerState.minuteInput = reminderTime.minute
            },
            confirmButton = {
                TextButton({
                    showReminderTimeDialog = false
                    setReminderTime(LocalTime.of(timePickerState.hour, timePickerState.minute))
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton({
                    showReminderTimeDialog = false
                    timePickerState.hourInput = reminderTime.hour
                    timePickerState.minuteInput = reminderTime.minute
                }) { Text("Cancel") }
            },
            title = { Text("Reminder Time") }
        ) { TimePicker(timePickerState) }
    }
}