package com.example.mainproject.NAVIGATION

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.mainproject.data.model.ListCategories
import com.example.mainproject.data.repository.AuthRepository
import com.example.mainproject.data.repository.NotificationRepository
import com.example.mainproject.ui.screens.AnalysisScreen
import com.example.mainproject.ui.screens.CategoriesScreen
import com.example.mainproject.ui.screens.CategoryDetailScreen
import com.example.mainproject.ui.screens.Home
import com.example.mainproject.ui.screens.ItemScreen
//import com.example.mainproject.ui.screens.ItemScreen
import com.example.mainproject.ui.screens.MainScreen
import com.example.mainproject.ui.screens.NotificationScreen
import com.example.mainproject.ui.screens.SignIn
import com.example.mainproject.ui.screens.SignUp
import com.example.mainproject.ui.screens.SplashScreen
import com.example.mainproject.ui.screens.TransactionScreen
import com.example.mainproject.viewModel.AppViewModel
import com.example.mainproject.viewModel.AppViewModelFactory
import com.example.mainproject.viewModel.AuthViewModel
import com.example.mainproject.viewModel.AuthViewModelFactory
import com.example.mainproject.viewModel.TransactionViewModel
import com.example.mainproject.viewModel.TransactionViewModelFactory
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

    val currentUserState = appViewModel.currentUser.collectAsState()
    val userId = currentUserState.value?.userId
    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(
            notificationRepository = NotificationRepository.create(),
            userId = userId
        )
    )

    //Luu trang thai dang nhap de chi dang nhap 1 lan trong tren thiet bi
    var startDestination = "splashScreen"
    if (FirebaseAuth.getInstance().currentUser != null)
    {
        startDestination = Routes.HOME
    }

    NavHost(navController = navController, startDestination = startDestination) {
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
        composable("categoryDetail/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            CategoryDetailScreen(
                navController,
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onAddExpenseClick = { navController.navigate("addExpense/$categoryId") }
            )
        }

        composable(
            route = "signIn"
        ) { backStackEntry ->
            SignIn(
                navController = navController,
                viewModel = authViewModel
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
//        composable(route = Routes.PROFILE) {
//            ProfileScreen(navController = navController)
//        }
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
             ItemScreen(navController = navController, listItem = listItem, viewModel = transactionViewModel)
        }
        composable(route = Routes.TRANSACTION) {
            TransactionScreen(navController = navController)
        }
        composable(route = Routes.NOTIFICATION) {
            NotificationScreen(navController = navController, appViewModel = appViewModel)
        }
        composable(route = Routes.ANALYTICS) {
            AnalysisScreen(navController = navController)
        }
    }
}