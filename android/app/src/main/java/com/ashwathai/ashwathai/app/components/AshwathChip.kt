package com.ashwathai.ashwathai.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ashwathai.ashwathai.app.theme.BorderSubtle
import com.ashwathai.ashwathai.app.theme.SurfaceTier2

@Composable
fun AshwathChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceTier2, RoundedCornerShape(2.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(2.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
