package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.NavigationItem

@Composable
fun NotificationSettingsScreen(navController: NavController) {
    NotificationSettingsContent(navController)
}

@Composable
fun NotificationSettingsContent(navController: NavController) {
    val switchStates = remember {
        mutableStateListOf(true, true, true, true, false, false, false, false)
    }

    val titles = listOf(
        "General Notification",
        "Sound",
        "Sound Call",
        "Vibrate",
        "Transaction Update",
        "Expense Reminder",
        "Budget Notifications",
        "Low Balance Alerts"
    )

    var selectedItem by remember { mutableStateOf(NavigationItem.DefaultItems[0].route) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF3498DB))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    "Notification Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
        }

        // Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFF4FFF9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                titles.forEachIndexed { index, title ->
                    NotificationItem(title = title, checked = switchStates[index]) {
                        switchStates[index] = it
                    }
                }
            }
        }

        // Bottom Nav
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemClick = { item ->
                    selectedItem = item.route
                    navController.navigate(item.route)
                }
            )
        }
    }
}

@Composable
fun NotificationItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3498DB))
        )
    }
}
