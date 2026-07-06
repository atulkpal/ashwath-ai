package com.ashwathai.ashwathai.core

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.di.EngineConfigProvider
import com.ashwathai.ashwathai.di.EngineMode
import com.ashwathai.sdk.jni.AshwathBridge
import kotlinx.coroutines.delay

enum class EngineConnectionState {
    Checking, Connected, Disconnected, Unavailable
}

@Composable
fun rememberEngineState(): State<EngineConnectionState> {
    val config = remember { EngineConfigProvider.get() }
    return produceState(initialValue = EngineConnectionState.Checking) {
        if (config.mode == EngineMode.EMBEDDED && !AshwathBridge.Companion.isLoaded) {
            value = EngineConnectionState.Unavailable
            return@produceState
        }
        delay(2000)
        value = EngineConnectionState.Connected
    }
}

@Composable
fun EngineStatusBanner(state: EngineConnectionState) {
    AnimatedVisibility(
        visible = state != EngineConnectionState.Connected && state != EngineConnectionState.Checking,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        val (message, color) = when (state) {
            EngineConnectionState.Unavailable -> "Engine not installed" to Color(0xFFEF4444)
            EngineConnectionState.Disconnected -> "Engine disconnected" to Color(0xFFEAB308)
            else -> "" to Color.Transparent
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = color
                )
            }
        }
    }
}
