package com.example.mainproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemClick: (NavigationItem) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFFEFFFF6)),
        containerColor = Color(0xFFEFFFF6),
        tonalElevation = 0.dp
    ) {
        NavigationItems.DefaultItems.forEach { item ->
            val selected = selectedItem == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        tint = if (selected) Color(0xFF3498DB) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selected) Color(0xFF3498DB) else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF3498DB),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFF3498DB),
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFEFFFF6)
                )
            )
        }
    }
} 