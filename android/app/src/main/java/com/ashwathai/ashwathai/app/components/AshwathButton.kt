package com.ashwathai.ashwathai.app.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ashwathai.ashwathai.app.theme.BorderSubtle
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.PureBlack

@Composable
fun AshwathPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "buttonPress"
    )

    Button(
        onClick = onClick,
        modifier = modifier.offset(y = offset),
        enabled = enabled,
        shape = RoundedCornerShape(4.dp), // Soft-Sharp
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = CyanPrimary,
            contentColor = PureBlack,
            disabledContainerColor = Color.DarkGray,
            disabledContentColor = Color.Gray
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun AshwathSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "buttonPress"
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.offset(y = offset),
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        interactionSource = interactionSource,
        border = BorderStroke(1.dp, BorderSubtle),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            disabledContentColor = Color.Gray
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
