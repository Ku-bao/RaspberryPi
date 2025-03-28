package com.example.raspberrypi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.raspberrypi.ui.screens.ControlScreen
import com.example.raspberrypi.ui.screens.MainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("control") {
            ControlScreen(navController)
        }
    }
} 