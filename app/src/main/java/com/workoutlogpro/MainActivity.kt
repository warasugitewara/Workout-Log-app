package com.workoutlogpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.workoutlogpro.ui.navigation.AppNavigation
import com.workoutlogpro.ui.theme.WorkoutLogProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkoutLogProTheme {
                AppNavigation()
            }
        }
    }
}
