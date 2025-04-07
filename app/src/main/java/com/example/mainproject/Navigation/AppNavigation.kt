package com.example.mainproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mainproject.Navigation.Routes
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
            MainScreen(navController) // Truyền navController vào MainScreen
        }
        composable(Routes.SIGN_IN) {
            SignIn(navController) // Màn hình Sign In
        }
        composable(Routes.SIGN_UP) {
            SignUp(navController) // Màn hình Sign Up
        }
    }
}

