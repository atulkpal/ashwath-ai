package com.ashwathai.ashwathai.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ashwathai.ashwathai.app.theme.BorderSubtle
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.SurfaceTier2

@Composable
fun AshwathTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = singleLine,
        placeholder = { Text(placeholder, color = Color.Gray) },
        shape = RoundedCornerShape(4.dp), // Soft-Sharp
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceTier2,
            unfocusedContainerColor = SurfaceTier2,
            disabledContainerColor = SurfaceTier2,
            focusedBorderColor = CyanPrimary,
            unfocusedBorderColor = BorderSubtle,
            disabledBorderColor = BorderSubtle,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledTextColor = Color.Gray
        )
    )
}
