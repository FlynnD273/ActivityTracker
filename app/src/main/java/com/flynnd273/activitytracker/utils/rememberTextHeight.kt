package com.flynnd273.activitytracker.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp

@Composable
fun rememberTextHeight(
    text: String,
    style: TextStyle
): Dp {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val layoutResult = textMeasurer.measure(
        text = text,
        style = style
    )

    return with(density) {
        layoutResult.size.height.toDp()
    }
}