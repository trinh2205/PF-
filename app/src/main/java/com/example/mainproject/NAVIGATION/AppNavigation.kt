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
import com.example.mainproject.data.repository.UserRepository
import com.example.mainproject.ui.screens.AnalysisScreen
import com.example.mainproject.ui.screens.BankScreen
import com.example.mainproject.ui.screens.CategoriesScreen
import com.example.mainproject.ui.screens.CategoryDetailScreen
import com.example.mainproject.ui.screens.EditProfile
import com.example.mainproject.ui.screens.Home
import com.example.mainproject.ui.screens.ItemScreen
//import com.example.mainproject.ui.screens.ItemScreen
import com.example.mainproject.ui.screens.MainScreen
import com.example.mainproject.ui.screens.NotificationScreen
import com.example.mainproject.ui.screens.ProfileScreen
import com.example.mainproject.ui.screens.SaveBankScreen
import com.example.mainproject.ui.screens.SignIn
import com.example.mainproject.ui.screens.SignUp
import com.example.mainproject.ui.screens.SplashScreen
//import com.example.mainproject.ui.screens.TransactionHistoryScreen
import com.example.mainproject.viewModel.AppViewModel
import com.example.mainproject.viewModel.AppViewModelFactory
import com.example.mainproject.viewModel.AuthViewModel
import com.example.mainproject.viewModel.AuthViewModelFactory
import com.example.mainproject.viewModel.BankViewModel
import com.example.mainproject.viewModel.BankViewModelFactory
import com.example.mainproject.viewModel.EditProfileViewModel
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
    val userRepository = remember { UserRepository() } // Tạo UserRepository ở đây

    // Tạo BankViewModel ở đây
    val bankViewModelFactory = remember(userRepository) {
        BankViewModelFactory(
            userRepository = userRepository,
            userIdProvider = { auth.currentUser?.uid }
        )
    }
    val bankViewModel: BankViewModel = viewModel(factory = bankViewModelFactory)

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
        // Thêm composable cho SaveBankScreen
        composable(route = Routes.SAVE_BANK) {
            SaveBankScreen(navController = navController, bankViewModel = bankViewModel)
        }

        composable(route = Routes.MAIN_SCREEN) {
            MainScreen(navController = navController)
        }
        composable("categoryDetail/{ListCategoryId}") { backStackEntry ->
            val ListCategoryId = backStackEntry.arguments?.getString("ListCategoryId") ?: ""
            CategoryDetailScreen(
                navController,
                viewModel = transactionViewModel,
                ListCategoryId = ListCategoryId,
                onBack = { navController.popBackStack() },
                onAddExpenseClick = { navController.navigate("addExpense/$ListCategoryId") }
            )
        }

        composable(route = Routes.BANK) {
            BankScreen(navController = navController, bankViewModel = bankViewModel)
        }

        composable(
            route = Routes.SIGN_IN
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
        composable(route = Routes.PROFILE) {
            ProfileScreen(
                navController = navController,
                appViewModel = appViewModel,
                editProfileViewModel = viewModel(
                    factory = EditProfileViewModel.provideFactory(
                        userRepository = userRepository, // Truyền userRepository
                        appViewModel = appViewModel,
                        auth = FirebaseAuth.getInstance()
                    )
                )
            )
        }
//        composable(
//            route = "itemScreen/{listCategoryId}/{listCategoryName}",
//            arguments = listOf(
//                navArgument("listCategoryId") { type = NavType.StringType },
//                navArgument("listCategoryName") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val listCategoryId = backStackEntry.arguments?.getString("listCategoryId") ?: ""
//            val listCategoryName = backStackEntry.arguments?.getString("listCategoryName") ?: ""
//            val listItem = remember {
//                ListCategories(id = listCategoryId, name = listCategoryName, icon = Icons.Filled.Fastfood)
//            }
//             ItemScreen(navController = navController, listItem = listItem, viewModel = transactionViewModel)
//        }
//        composable(route = Routes.TRANSACTION) {
//            TransactionHistoryScreen(navController = navController)
//        }
        composable(route = Routes.NOTIFICATION) {
            NotificationScreen(navController = navController, appViewModel = appViewModel)
        }
        composable(route = Routes.ANALYTICS) {
            AnalysisScreen(navController = navController)
        }

        composable(route = Routes.EDIT_PROFILES) {
            EditProfile(navController = navController)
        }
    }
}