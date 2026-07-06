package com.ashwathai.ashwathai.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashwathai.ashwathai.app.theme.BorderSubtle
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.OnCyanPrimary
import com.ashwathai.ashwathai.app.theme.SurfaceTier1
import com.ashwathai.ashwathai.app.theme.SurfaceTier2

@Composable
fun AshwathChatBubble(
    text: String,
    isAi: Boolean,
    modifier: Modifier = Modifier
) {
    val contentAlignment = remember(isAi) {
        if (isAi) Alignment.CenterStart else Alignment.CenterEnd
    }
    val backgroundColor = remember(isAi) {
        if (isAi) SurfaceTier1 else CyanPrimary
    }
    val textColor = remember(isAi) {
        if (isAi) Color.White else OnCyanPrimary
    }
    val bubbleShape = remember(isAi) {
        RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp,
            bottomStart = if (isAi) 4.dp else 8.dp,
            bottomEnd = if (isAi) 8.dp else 4.dp
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = contentAlignment
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(bubbleShape)
                .background(backgroundColor)
                .then(
                    if (isAi) Modifier.border(1.dp, BorderSubtle, bubbleShape)
                    else Modifier
                )
                .height(IntrinsicSize.Min)
        ) {
            if (isAi) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .background(CyanPrimary)
                )
            }
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}
