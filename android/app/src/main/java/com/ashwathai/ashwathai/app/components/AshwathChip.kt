package com.ashwathai.ashwathai.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ashwathai.ashwathai.app.theme.BorderSubtle
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.PureBlack
import com.ashwathai.ashwathai.app.theme.SurfaceTier2

@Composable
fun AshwathChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = if (selected) CyanPrimary else SurfaceTier2
    val contentColor = if (selected) PureBlack else Color.White
    val borderModifier = if (selected) Modifier else Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(2.dp))

    val clickableModifier = if (onClick != null) {
        Modifier
            .clip(RoundedCornerShape(2.dp))
            .clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(clickableModifier)
            .background(backgroundColor, RoundedCornerShape(2.dp))
            .then(borderModifier)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}
