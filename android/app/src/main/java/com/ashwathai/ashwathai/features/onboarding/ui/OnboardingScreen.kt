package com.ashwathai.ashwathai.features.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashwathai.ashwathai.app.components.AshwathPrimaryButton
import com.ashwathai.ashwathai.app.theme.CyanPrimary

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Subtle radial gradient in background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(CyanPrimary.copy(alpha = 0.05f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(500f, 500f),
                        radius = 1000f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(CyanPrimary.copy(alpha = 0.1f), Color.Transparent)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "ASHWATH AI",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Intelligence remains on your device.\nAlways private. Always yours.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            AshwathPrimaryButton(
                text = "GET STARTED",
                onClick = onGetStarted,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Version 0.2.0 Stable",
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray,
                letterSpacing = 1.sp
            )
        }
    }
}
