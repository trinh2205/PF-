package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mainproject.R
import com.example.mainproject.ui.components.CustomHeader
import com.example.mainproject.ui.components.NavigationItem
import com.example.mainproject.ui.components.miniBox

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
fun AddMoneyScreen(navController: NavController) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.mainColor))
            .padding(16.dp)
    ) {
        CustomHeader(
            title = "Add Money",
            onBackClick = { navController.popBackStack() },
            hasNotifications = null,
            onNotificationClick = { }
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (VND)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { selectedDate = it },
                label = { Text("Date") },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            )

            OutlinedTextField(
                value = selectedTime,
                onValueChange = { selectedTime = it },
                label = { Text("Time") },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                // TODO: Handle adding money
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF4CAF50)
            )
        ) {
            Text(
                "Add Money",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun TransactionScreen(navController: NavController) {
    val transactionList = listOf(
        TransactionItem(
            month = "April",
            date = "30",
            time = "18:27",
            icon = Icons.Filled.ArrowUpward,
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
            "AddMoney" -> transactionList
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
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
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
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { filterType = "AddMoney" }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    miniBox(
                        onClick = { filterType = "AddMoney" },
                        icon = null,
                        title = "Total Balance",
                        money = transactionList.sumOf { it.amount },
                        colorBG = Color.White,
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
                        colorBG = Color(0xFFE8F5E9),
                        colorText = Color(0xFF4CAF50),
                        fillWidth = 0.dp,
                        fillHeight = 100.dp,
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable { filterType = "Income" },
                        isActive = filterType == "Income",
                        activeIconColor = Color.White,
                        activeTextColor = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    miniBox(
                        onClick = { filterType = "Expense" },
                        icon = Icons.Filled.ArrowDownward,
                        title = "Expense",
                        money = transactionList.filter { it.amount < 0 }.sumOf { it.amount },
                        colorBG = Color(0xFFFFEBEE),
                        colorText = Color(0xFFF44336),
                        fillWidth = 0.dp,
                        fillHeight = 100.dp,
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable { filterType = "Expense" },
                        isActive = filterType == "Expense",
                        activeIconColor = Color.White,
                        activeTextColor = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (filterType == "AddMoney") {
                AddMoneyScreen(
                    navController = navController,
                    onBackClick = { filterType = null }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .background(Color.White)
                ) {
                    LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)) {
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
                                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(if (transaction.amount > 0) Color(0xFF4CAF50) else Color(0xFFF44336)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            androidx.compose.material.Icon(
                                                imageVector = transaction.icon,
                                                contentDescription = transaction.description,
                                                tint = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                transaction.description,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                "${transaction.time} - ${transaction.date}",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Spacer(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .fillMaxHeight(0.7f)
                                                .background(Color.Gray.copy(alpha = 0.3f))
                                        )

                                        Text(
                                            text = transaction.category,
                                            color = Color.Gray,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )

                                        Spacer(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .fillMaxHeight(0.7f)
                                                .background(Color.Gray.copy(alpha = 0.3f))
                                        )

                                        Text(
                                            text = "${if (transaction.amount < 0) "-" else ""}${String.format("%,d", (Math.abs(transaction.amount) * 23000).toInt())} VND",
                                            fontWeight = FontWeight.Bold,
                                            color = if (transaction.amount > 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.padding(start = 12.dp)
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
}

@Composable
fun AddMoneyScreen(
    navController: NavController,
    onBackClick: () -> Unit
) {
    var amount by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.mainColor))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.White)
                .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Add Money",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() } },
                label = {
                    Text(
                        "Nhập số tiền (VND)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color(0xFFBDBDBD),
                    backgroundColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    // TODO: Handle adding money
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    "Xác nhận",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionScreenPreview() {
    val navController = rememberNavController()
    TransactionScreen(navController = navController)
}