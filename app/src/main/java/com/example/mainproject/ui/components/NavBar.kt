package com.example.mainproject.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Định nghĩa một lớp dữ liệu cho các mục trong thanh điều hướng
data class NavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

// Composable cho thanh điều hướng
@Composable
fun NavBar(navController: NavController, items: List<NavItem>) {
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}

// Sử dụng NavBar trong Scaffold
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navItems = listOf(
        NavItem("Home", Icons.Default.Home, "home"),
        NavItem("Search", Icons.Default.Search, "search"),
        NavItem("Profile", Icons.Default.Person, "profile")
    )

    Scaffold(
        bottomBar = { NavBar(navController = navController, items = navItems) }
    ) { innerPadding ->
        // Nội dung chính của màn hình
        // Sử dụng NavHost để hiển thị các màn hình tương ứng với route
    }
}
