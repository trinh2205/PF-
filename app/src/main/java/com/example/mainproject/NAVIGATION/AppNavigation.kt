package com.example.mainproject.NAVIGATION

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.mainproject.Data.model.ListCategories
import com.example.mainproject.Data.repository.AuthRepository
import com.example.mainproject.ui.auth.AuthViewModel
import com.example.mainproject.ui.screens.CategoriesScreen
import com.example.mainproject.ui.screens.Home
//import com.example.mainproject.ui.screens.ItemScreen
import com.example.mainproject.ui.screens.MainScreen
import com.example.mainproject.ui.screens.NotificationItem
import com.example.mainproject.ui.screens.NotificationScreen
import com.example.mainproject.ui.screens.NotificationSettingsScreen
import com.example.mainproject.ui.screens.ProfileScreen
import com.example.mainproject.ui.screens.SettingsScreen
import com.example.mainproject.ui.screens.SignIn
import com.example.mainproject.ui.screens.SignUp
import com.example.mainproject.ui.screens.SplashScreen
import com.example.mainproject.ui.screens.TransactionScreen
import com.example.mainproject.viewModel.AppViewModel
import com.example.mainproject.viewModel.AppViewModelFactory
import com.example.mainproject.viewModel.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(auth: FirebaseAuth, navController: NavHostController) {
    val authRepository = remember { AuthRepository() }
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )
    val appViewModel: AppViewModel = viewModel(
        factory = AppViewModelFactory(auth) // Truyền auth nếu AppViewModel cần
    )

    NavHost(navController = navController, startDestination = "splashScreen") {
        composable("splashScreen") {
            SplashScreen(onNavigateToMain = {
                navController.navigate(Routes.MAIN_SCREEN) {
                    popUpTo("splashScreen") { inclusive = true }
                }
            })
        }
        composable(route = Routes.MAIN_SCREEN) {
            MainScreen(navController = navController)
        }
        composable(
            route = "signIn/{email}/{password}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            val password = backStackEntry.arguments?.getString("password")
            SignIn(
                navController = navController,
                viewModel = authViewModel,
                emailFromSignUp = email,
                passwordFromSignUp = password
            )
        }
        composable(route = Routes.SIGN_UP) {
            SignUp(navController = navController, authViewModel = authViewModel)
        }
        composable(route = Routes.HOME) {
            Home(navController = navController, auth = auth)
        }
        composable(route = Routes.CATEGORIES) {
            CategoriesScreen(navController = navController)
        }
        composable(route = Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }
        composable(
            route = "itemScreen/{listCategoryId}/{listCategoryName}",
            arguments = listOf(
                navArgument("listCategoryId") { type = NavType.StringType },
                navArgument("listCategoryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listCategoryId = backStackEntry.arguments?.getString("listCategoryId") ?: ""
            val listCategoryName = backStackEntry.arguments?.getString("listCategoryName") ?: ""
            val listItem = remember {
                ListCategories(id = listCategoryId, name = listCategoryName, icon = Icons.Filled.Fastfood)
            }
            // ItemScreen(navController = navController, listItem = listItem)
        }
        composable(route = Routes.TRANSACTION) {
            TransactionScreen(navController = navController)
        }
        composable(route = Routes.NOTIFICATION) {
            NotificationScreen(navController = navController, appViewModel = appViewModel)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController)
        }
        composable(Routes.NOTIFICATION_SETTINGS) {
            NotificationSettingsScreen(navController)
        }
    }
}