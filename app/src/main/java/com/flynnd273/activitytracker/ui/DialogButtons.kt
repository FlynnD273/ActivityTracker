package com.flynnd273.activitytracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> ColumnScope.DialogButtons(
    onDismissRequest: (T) -> Unit,
    value: T,
    color: T
) {
    Spacer(modifier = Modifier.weight(1f, true))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { onDismissRequest(value) }) {
            Icon(Icons.Default.Close, "Cancel")
        }
        IconButton(onClick = { onDismissRequest(color) }) {
            Icon(Icons.Default.Check, "Confirm")
        }
    }
}