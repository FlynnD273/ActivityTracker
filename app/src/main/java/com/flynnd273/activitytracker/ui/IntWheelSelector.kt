package com.flynnd273.activitytracker.ui

import android.content.res.Resources.getSystem
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.flynnd273.activitytracker.utils.rememberTextHeight
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun IntWheelSelector(
    textStyle: TextStyle,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
) {
    val textHeight = rememberTextHeight("00", textStyle) + 4.dp
    val textHeightPx = (textHeight.value * getSystem().displayMetrics.density).toInt()
    val numDigits = range.endInclusive.toString().length
    val items = range.toList().map {
        it.toString().padStart(numDigits, '0')
    }
    val state = rememberLazyListState((value - range.start) + items.size - 1)
    val flingBehavior = rememberSnapFlingBehavior(state)
    val scope = rememberCoroutineScope()

    val centerIndex by remember {
        derivedStateOf { state.firstVisibleItemIndex + 1 }
    }
    val centerOffset by remember { derivedStateOf { state.firstVisibleItemScrollOffset / textHeightPx.toFloat() } }

    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex }.collect {
            onValueChange(((it + 1) % items.size) - range.first)

            if (it < items.size) {
                scope.launch {
                    state.scrollToItem(it + items.size)
                }
            } else if (it > items.size * 2) {
                scope.launch {
                    state.scrollToItem(it - items.size)
                }
            }
        }
    }

    Column() {
        LazyColumn(
            state = state,
            flingBehavior = flingBehavior,
            modifier = Modifier.height(textHeight * 3),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items.size * 3) { i ->
                val item = items[i % items.size]

                val fraction = abs(i - (centerIndex + centerOffset))

                val scale = 1f - fraction * 0.3f
                val alpha = 1f - fraction * 0.7f
                Text(
                    item, style = textStyle, modifier = Modifier
                        .height(textHeight)
                        .graphicsLayer {
                            this.alpha = alpha
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }
        }
    }
}