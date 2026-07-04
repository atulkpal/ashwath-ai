package com.ashwathai.ashwathai.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.PureBlack
import com.ashwathai.ashwathai.app.theme.SurfaceTier2

@Composable
fun AshwathChatBubble(
    text: String,
    isAi: Boolean,
    modifier: Modifier = Modifier
) {
    val alignment = if (isAi) Alignment.Start else Alignment.End
    val backgroundColor = if (isAi) SurfaceTier2 else CyanPrimary
    val textColor = if (isAi) Color.White else PureBlack

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .clip(
                    RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = if (isAi) 0.dp else 8.dp,
                        bottomEnd = if (isAi) 8.dp else 0.dp
                    )
                )
                .background(backgroundColor)
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
                modifier = Modifier.padding(12.dp),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
