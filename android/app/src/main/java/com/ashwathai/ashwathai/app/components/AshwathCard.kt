package com.ashwathai.ashwathai.app.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ashwathai.ashwathai.app.theme.BorderSubtle
import com.ashwathai.ashwathai.app.theme.SurfaceTier1

@Composable
fun AshwathCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
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
