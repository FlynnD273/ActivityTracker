package com.flynnd273.activitytracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

val pickableColors = listOf(null, Color.Red, Color.Yellow, Color.Blue, Color.Green, Color.Cyan, Color.Magenta)

@Composable
fun ColorPickerDialog(value: Color?, onDismissRequest: (Color?) -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest(value) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4), modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(pickableColors) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .background(it ?: MaterialTheme.colorScheme.primary)
                            .clickable(onClick = { onDismissRequest(it) })
                    )
                }
            }
        }
    }
}