package com.ashwathai.ashwathai.app.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ashwathai.ashwathai.app.theme.BorderSubtle
import com.ashwathai.ashwathai.app.theme.SurfaceTier1

@Composable
fun AshwathCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(
        targetValue = if (isPressed && onClick != null) 1.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "cardPress"
    )

    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        interactionSource = interactionSource,
        modifier = modifier
            .offset(y = offset)
            .border(
                width = 1.dp,
                color = BorderSubtle,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = SurfaceTier1
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
