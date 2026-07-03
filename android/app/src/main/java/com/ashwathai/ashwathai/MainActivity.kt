package com.ashwathai.ashwathai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ashwathai.ashwathai.app.MainScreen
import com.ashwathai.ashwathai.app.theme.AshwathAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AshwathAITheme {
                MainScreen()
            }
        }
    }
}
