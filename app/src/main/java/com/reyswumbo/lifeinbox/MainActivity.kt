package com.reyswumbo.lifeinbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.reyswumbo.lifeinbox.ui.LifeInboxApp
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeInboxTheme {
                LifeInboxApp()
            }
        }
    }
}
