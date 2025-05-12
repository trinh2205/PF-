package com.example.mainproject.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mainproject.NAVIGATION.Routes

sealed class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    object Home : NavigationItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        route = Routes.HOME
    )
    
    object Categories : NavigationItem(
        title = "Categories",
        selectedIcon = Icons.Filled.Category,
        unselectedIcon = Icons.Outlined.Category,
        route = Routes.CATEGORIES
    )
    
    object Transaction : NavigationItem(
        title = "Transaction",
        selectedIcon = Icons.Filled.SwapHoriz,
        unselectedIcon = Icons.Outlined.SwapHoriz,
        route = Routes.TRANSACTION
    )
    
    object Profile : NavigationItem(
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        route = Routes.PROFILE
    )

    companion object {
        val DefaultItems = listOf(
            Home,
            Categories,
            Transaction,
            Profile
        )
    }
} 