package com.example.mainproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mainproject.NAVIGATION.Routes
import com.example.mainproject.ui.screens.*
import com.example.mainproject.ui.theme.MainProjectTheme

//import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = Routes.SPLASH_SCREEN
                    ) {
                        composable(Routes.SPLASH_SCREEN) {
                            SplashScreen(navController = navController)
                        }
                        composable(Routes.MAIN_SCREEN) {
                            MainScreen(navController = navController)
                        }
                        composable(Routes.SIGN_IN) {
                            SignInScreen(navController = navController)
                        }
                        composable(Routes.SIGN_UP) {
                            SignUpScreen(navController = navController)
                        }
                        composable(Routes.HOME) {
                            HomeScreen(navController = navController)
                        }
                        composable(Routes.CATEGORIES) {
                            CategoriesScreen(navController = navController)
                        }
                        composable(Routes.TRANSACTION) {
                            TransactionScreen(navController = navController)
                        }
                        composable(Routes.PROFILE) {
                            ProfileScreen(navController = navController)
                        }
                        composable(Routes.SETTINGS) {
                            SettingsScreen(navController = navController)
                        }
                        composable(Routes.NOTIFICATION_SETTINGS) {
                            NotificationSettingsScreen(navController = navController)
                        }
                        composable(Routes.EDIT_PROFILE) {
                            EditProfileScreen(navController = navController)
                        }
                        composable(Routes.PASSWORD_SETTINGS) {
                            PasswordSettingsScreen(navController = navController)
                        }
                        composable(Routes.DELETE_ACCOUNT) {
                            DeleteAccountScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}