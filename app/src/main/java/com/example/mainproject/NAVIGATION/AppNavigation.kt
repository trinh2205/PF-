package com.example.mainproject.NAVIGATION

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mainproject.ui.screens.Home
import com.example.mainproject.ui.screens.MainScreen
import com.example.mainproject.ui.screens.SignIn
import com.example.mainproject.ui.screens.SignUp
import com.example.mainproject.ui.screens.SplashScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splashScreen") {
        composable("splashScreen") {
            SplashScreen(onNavigateToMain = {
                navController.navigate("mainScreen") {
                    popUpTo("splashScreen") { inclusive = true }
                }
            })
        }
        composable(Routes.MAIN_SCREEN) {
            MainScreen(navController = navController) // Truyền navController vào MainScreen
        }
        composable(Routes.SIGN_IN) {
            SignIn(navController = navController) // Màn hình Sign In
        }
        composable(Routes.SIGN_UP) {
            SignUp(navController = navController) // Màn hình Sign Up
        }
        composable(Routes.HOME) {
            Home(navController = navController) // Màn hình Home
        }
    }
}

