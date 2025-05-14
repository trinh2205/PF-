package com.example.mainproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainproject.NAVIGATION.Routes

data class NavigationItem(
    val icon: ImageVector,
    val route: String, // Để xử lý navigation sau này
    val label: String? = null // Tùy chọn label
) {
    companion object {
        val DefaultItems = listOf(
            NavigationItem(icon = Icons.Default.Home, route = Routes.HOME),
            NavigationItem(icon = Icons.Default.BarChart, route = Routes.ANALYTICS),
            NavigationItem(icon = Icons.Default.SwapHoriz, route = Routes.TRANSACTION),
            NavigationItem(icon = Icons.Default.Layers, route = Routes.CATEGORIES),
            NavigationItem(icon = Icons.Default.Person, route = Routes.PROFILE),
            NavigationItem(icon = Icons.Default.AccountBalance, route = Routes.BANK, label = "Ngân hàng") // Thêm item cho BankScreen
        )
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemClick: (NavigationItem) -> Unit,
    items: List<NavigationItem> = NavigationItem.DefaultItems
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Transparent) // Màu nền trong suốt cho Box ngoài
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFDFF7E2))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 30.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = selectedItem == item.route
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF3498DB) else Color.Transparent)
                            .clickable { onItemClick(item) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) Color.White else Color(0xFF052224),
                                modifier = Modifier.size(26.dp)
                            )
                            item.label?.let {
                                Text(
                                    text = it,
                                    color = if (isSelected) Color.White else Color(0xFF052224),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Bottom Navigation Bar")
@Composable
fun BottomNavigationBarPreview() {
    var selectedItem by remember { mutableStateOf(NavigationItem.DefaultItems.first().route) }
    BottomNavigationBar(
        selectedItem = selectedItem,
        onItemClick = { newItem -> selectedItem = newItem.route }
    )
}

@Preview(showBackground = true, name = "Bottom Navigation Bar Selected Item 1")
@Composable
fun BottomNavigationBarSelectedItem1Preview() {
    var selectedItem by remember { mutableStateOf(NavigationItem.DefaultItems[1].route) }
    BottomNavigationBar(
        selectedItem = selectedItem,
        onItemClick = { } // Trong preview, onItemClick có thể để trống
    )
}
