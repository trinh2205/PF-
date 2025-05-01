package com.example.mainproject.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainproject.ui.components.BottomNavigationBar
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mainproject.R
import com.example.mainproject.ui.components.CustomHeader
import com.example.mainproject.ui.components.NavigationItem
import com.example.mainproject.ui.components.miniBox
import org.xmlpull.v1.sax2.Driver

data class TransactionItem(
    val month: String,
    val date: String,
    val time: String,
    val icon: ImageVector,
    val description: String,
    val category: String,
    val amount: Double
)

@Composable
fun TransactionScreen(navController: NavController) {
    val transactionList = listOf(
        TransactionItem(
            month = "April",
            date = "30",
            time = "18:27",
            icon = Icons.Filled.ArrowUpward, // Sử dụng icon mũi tên lên cho Income
            description = "Salary",
            category = "Monthly",
            amount = 4000.00
        ),
        TransactionItem(
            month = "April",
            date = "24",
            time = "17:00",
            icon = Icons.Filled.ShoppingCart,
            description = "Groceries",
            category = "Pantry",
            amount = -100.00
        ),
        TransactionItem(
            month = "April",
            date = "15",
            time = "8:30",
            icon = Icons.Filled.Home,
            description = "Rent",
            category = "Rent",
            amount = -674.40
        ),
        TransactionItem(
            month = "April",
            date = "08",
            time = "7:30",
            icon = Icons.Filled.DirectionsBus,
            description = "Transport",
            category = "Fuel",
            amount = -4.13
        ),
        TransactionItem(
            month = "March",
            date = "31",
            time = "19:30",
            icon = Icons.Filled.Restaurant,
            description = "Food",
            category = "Dinner",
            amount = -70.40
        )
    )

    var selectedTab by remember { mutableStateOf("transfer") }
    var filterType by remember { mutableStateOf<String?>(null) }

    val filteredTransactions = remember(transactionList, filterType) {
        when (filterType) {
            "Income" -> transactionList.filter { it.amount > 0 }
            "Expense" -> transactionList.filter { it.amount < 0 }
            else -> transactionList
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = currentRoute ?: NavigationItem.DefaultItems.first().route,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        // ... cấu hình điều hướng (tùy chọn) ...
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(colorResource(id = R.color.mainColor))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                CustomHeader(
                    title = "Transaction",
                    onBackClick = { navController.popBackStack() },
                    hasNotifications = null,
                    onNotificationClick = { println("Notification icon clicked!") }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    miniBox(
                        onClick = {
                            filterType = null
                        }, // Đặt filterType về null để hiển thị tất cả
                        icon = null,
                        title = "Total Balance",
                        money = transactionList.sumOf { it.amount }, // Tính tổng thực tế
                        colorBG = Color(0xFFFFFFFF),
                        colorText = Color.Black,
                        fillWidth = 0.dp,
                        fillHeight = 72.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    miniBox(
                        onClick = { filterType = "Income" },
                        icon = Icons.Filled.ArrowUpward,
                        title = "Income",
                        money = transactionList.filter { it.amount > 0 }.sumOf { it.amount },
                        colorBG = Color(0xFFF4FFF9),
                        colorText = Color.Black,
                        fillWidth = 0.dp,
                        fillHeight = 100.dp,
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable { filterType = "Income" },
                        isActive = filterType == "Income",
                        activeIconColor = Color.White,
                        activeTextColor = Color.White// Thêm Modifier.clickable
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    miniBox(
                        onClick = { filterType = "Expense" },
                        icon = Icons.Filled.ArrowDownward,
                        title = "Expense",
                        money = transactionList.filter { it.amount < 0 }.sumOf { it.amount },
                        colorBG = Color(0xFFF4FFF9),
                        colorText = colorResource(id = R.color.mainColor),
                        fillWidth = 0.dp,
                        fillHeight = 100.dp,
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable { filterType = "Expense" },
                        isActive = filterType == "Expense",
                        activeIconColor = Color.White,
                        activeTextColor = Color.White// Thêm Modifier.clickable
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                    .background(Color(0xFFE6FFF9))
            ) {
                LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 40.dp)) {
                    val groupedTransactions = filteredTransactions.groupBy { it.month }
                    groupedTransactions.forEach { (month, transactions) ->
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                month,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(transactions) { transaction ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF4C9CDA)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        androidx.compose.material.Icon(
                                            imageVector = transaction.icon,
                                            contentDescription = transaction.description,
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(transaction.description, fontWeight = FontWeight.Bold)
                                        Text(
                                            "${transaction.time} - ${transaction.date}",
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .fillMaxHeight(0.7f) // Điều chỉnh tỷ lệ chiều cao nếu cần
                                            .background(Color.Black)
                                    ) // Đường kẻ dọc

                                    Text(
                                        text = "${transaction.category}",
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 8.dp) // Thêm padding
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .fillMaxHeight(0.7f) // Điều chỉnh tỷ lệ chiều cao nếu cần
                                            .background(Color.Black)
                                            .padding(start = 8.dp) // Thêm padding
                                    ) // Đường kẻ dọc

                                    Text(
                                        text = "${if (transaction.amount < 0) "-" else ""}$${
                                            String.format(
                                                "%.2f",
                                                Math.abs(transaction.amount)
                                            )
                                        }",
                                        fontWeight = FontWeight.Bold,
                                        color = if (transaction.amount > 0) Color(0xFF4CAF50) else Color(
                                            0xFFF44336
                                        ),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.padding(start = 8.dp) // Thêm padding
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//    @Preview(showBackground = true, showSystemUi = true)
//    @Composable
//    fun TransactionScreenPreview() {
//        val navController = rememberNavController()
//        TransactionScreen(navController = navController)
//    }