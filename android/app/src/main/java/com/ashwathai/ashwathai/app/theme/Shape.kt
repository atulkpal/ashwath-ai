package com.ashwathai.ashwathai.app.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Ashwath.AI Shape System: "Soft-Sharp"
// Based on docs/design/android/v1/theme/DESIGN.md
// No pills allowed. All rounding is subtle (4px or 8px).

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),    // rounded-sm equivalent
    medium = RoundedCornerShape(4.dp),   // rounded-md equivalent
    large = RoundedCornerShape(8.dp),    // rounded-lg equivalent
    extraLarge = RoundedCornerShape(12.dp) // rounded-xl equivalent
)
