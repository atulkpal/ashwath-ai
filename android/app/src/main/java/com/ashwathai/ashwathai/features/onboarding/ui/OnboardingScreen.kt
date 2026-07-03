package com.ashwathai.ashwathai.features.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashwathai.ashwathai.app.theme.CyanPrimary

@Composable
fun OnboardingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ASHWATH.AI",
            style = MaterialTheme.typography.headlineLarge,
            color = CyanPrimary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your private, offline AI companion.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { /* Navigate to Explore */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black)
        ) {
            Text("GET STARTED")
        }
    }
}
