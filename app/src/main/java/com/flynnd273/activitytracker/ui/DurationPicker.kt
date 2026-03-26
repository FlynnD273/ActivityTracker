package com.flynnd273.activitytracker.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DurationPicker(
    seconds: Int,
    onDurationChange: (Int) -> Unit
) {
    var hours by remember { mutableStateOf(seconds / 60 / 60) }
    var minutes by remember { mutableStateOf(seconds / 60) }
    val textStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)

    LaunchedEffect(Unit) {
        snapshotFlow { hours to minutes }.collect { (h, s) ->
            onDurationChange(h * 60 * 60 + s * 60)
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IntWheelSelector(
            textStyle = textStyle,
            value = hours,
            range = (0..24),
            onValueChange = { hours = it },
        )
        Text(" : ", style = textStyle, modifier = Modifier.offset(y = -(4.dp)))
        IntWheelSelector(
            textStyle = textStyle,
            value = minutes,
            range = (0..59),
            onValueChange = { minutes = it },
        )
    }
}