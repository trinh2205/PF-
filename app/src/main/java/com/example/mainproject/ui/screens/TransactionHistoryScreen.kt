//package com.example.mainproject.ui.screens
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowDownward
//import androidx.compose.material.icons.filled.ArrowDropDown
//import androidx.compose.material.icons.filled.ArrowDropUp
//import androidx.compose.material.icons.filled.ArrowUpward
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.res.colorResource
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.example.mainproject.Data.model.Transaction
//import com.example.mainproject.R
//import com.example.mainproject.ui.components.BottomNavigationBar
//import com.example.mainproject.ui.components.CustomHeader
//import com.example.mainproject.ui.components.NavigationItem
//import com.example.mainproject.ui.components.miniBox
//import com.example.mainproject.viewModel.TransactionViewModel
//import java.text.SimpleDateFormat
//import java.util.*
//
//data class TransactionItem(
//    val month: String,
//    val date: String,
//    val time: String,
//    val icon: androidx.compose.ui.graphics.vector.ImageVector,
//    val description: String,
//    val category: String,
//    val amount: Double
//)
//
//@Composable
//fun TransactionScreen(
//    navController: NavController,
//    viewModel: TransactionViewModel = viewModel()
//) {
//    // Thu thập dữ liệu từ ViewModel
//    val transactions by viewModel.transactions.collectAsState()
//    val transactionBE by viewModel.transactionBE.collectAsState()
//    val expenses by viewModel.expenses
//    val categories by viewModel.categories.collectAsState()
//
//    // Chuyển đổi Transaction, TransactionBE và Expense thành TransactionItem
//    val transactionItems by remember(transactions, transactionBE, expenses, categories) {
//        derivedStateOf {
//            val items = mutableListOf<TransactionItem>()
//
//            // Xử lý Transactions
//            transactions.forEach { (id, transaction) ->
//                val date = parseDate(transaction.date)
//                items.add(
//                    TransactionItem(
//                        month = SimpleDateFormat("MMMM", Locale.getDefault()).format(date),
//                        date = SimpleDateFormat("dd", Locale.getDefault()).format(date),
//                        time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date),
//                        icon = if (transaction.isPositive) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
//                        description = transaction.title,
//                        category = transaction.period,
//                        amount = if (transaction.isPositive) transaction.amount.toDouble() else -transaction.amount.toDouble()
//                    )
//                )
//            }
//
//            // Xử lý TransactionBE
//            transactionBE.forEach { transaction ->
//                val date = parseDate(transaction.date)
//                items.add(
//                    TransactionItem(
//                        month = SimpleDateFormat("MMMM", Locale.getDefault()).format(date),
//                        date = SimpleDateFormat("dd", Locale.getDefault()).format(date),
//                        time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date),
//                        icon = if (transaction.isPositive) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
//                        description = transaction.title,
//                        category = transaction.period,
//                        amount = if (transaction.isPositive) transaction.amount.toDouble() else transaction.amount.toDouble()
//                    )
//                )
//            }
//
//            // Xử lý Expenses
//            expenses.forEach { (categoryId, expenseList) ->
//                val categoryName = categories[categoryId]?.name ?: "Unknown"
//                expenseList.forEach { expense ->
//                    val date = parseDate(expense.date)
//                    items.add(
//                        TransactionItem(
//                            month = SimpleDateFormat("MMMM", Locale.getDefault()).format(date),
//                            date = SimpleDateFormat("dd", Locale.getDefault()).format(date),
//                            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date),
//                            icon = Icons.Filled.ArrowDownward,
//                            description = expense.title,
//                            category = categoryName,
//                            amount = -expense.amount
//                        )
//                    )
//                }
//            }
//
//            // Sắp xếp theo ngày (mới nhất trước)
//            items.sortedByDescending { parseDate("${it.date} ${it.month}").time }
//        }
//    }
//
//    var filterType by remember { mutableStateOf<String?>(null) }
//    var showAddIncomeDialog by remember { mutableStateOf(false) }
//
//    // Lọc giao dịch theo loại
//    val filteredTransactions = remember(transactionItems, filterType) {
//        when (filterType) {
//            "Income" -> transactionItems.filter { it.amount > 0 }
//            "Expense" -> transactionItems.filter { it.amount < 0 }
//            else -> transactionItems
//        }
//    }
//
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    Scaffold(
//        bottomBar = {
//            BottomNavigationBar(
//                selectedItem = currentRoute ?: NavigationItem.DefaultItems.first().route,
//                onItemClick = { item ->
//                    navController.navigate(item.route) {
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                }
//            )
//        },
//        floatingActionButton = {
//            if (filterType == "Income") {
//                FloatingActionButton(
//                    onClick = { showAddIncomeDialog = true },
//                    backgroundColor = Color(0xFF4CAF50),
//                    contentColor = Color.White
//                ) {
//                    Icon(Icons.Filled.Add, contentDescription = "Add Income")
//                }
//            }
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//                .background(colorResource(id = R.color.mainColor))
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//            ) {
//                CustomHeader(
//                    title = "Transaction",
//                    onBackClick = { navController.popBackStack() },
//                    hasNotifications = null,
//                    onNotificationClick = { println("Notification icon clicked!") }
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.White, RoundedCornerShape(16.dp)),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    miniBox(
//                        onClick = { filterType = null },
//                        icon = null,
//                        title = "Total Balance",
//                        money = transactionItems.sumOf { it.amount },
//                        colorBG = Color(0xFFFFFFFF),
//                        colorText = Color.Black,
//                        fillWidth = 0.dp,
//                        fillHeight = 72.dp,
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    miniBox(
//                        onClick = { filterType = "Income" },
//                        icon = Icons.Filled.ArrowUpward,
//                        title = "Income",
//                        money = transactionItems.filter { it.amount > 0 }.sumOf { it.amount },
//                        colorBG = Color(0xFFF4FFF9),
//                        colorText = Color.Black,
//                        fillWidth = 0.dp,
//                        fillHeight = 100.dp,
//                        modifier = Modifier
//                            .weight(0.5f)
//                            .clickable { filterType = "Income" },
//                        isActive = filterType == "Income",
//                        activeIconColor = Color.White,
//                        activeTextColor = Color.White
//                    )
//
//                    Spacer(modifier = Modifier.width(8.dp))
//                    miniBox(
//                        onClick = { filterType = "Expense" },
//                        icon = Icons.Filled.ArrowDownward,
//                        title = "Expense",
//                        money = transactionItems.filter { it.amount < 0 }.sumOf { it.amount },
//                        colorBG = Color(0xFFF4FFF9),
//                        colorText = colorResource(id = R.color.mainColor),
//                        fillWidth = 0.dp,
//                        fillHeight = 100.dp,
//                        modifier = Modifier
//                            .weight(0.5f)
//                            .clickable { filterType = "Expense" },
//                        isActive = filterType == "Expense",
//                        activeIconColor = Color.White,
//                        activeTextColor = Color.White
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//                    .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
//                    .background(Color(0xFFE6FFF9))
//            ) {
//                LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 40.dp)) {
//                    val groupedTransactions = filteredTransactions.groupBy { it.month }
//                    groupedTransactions.forEach { (month, transactions) ->
//                        item {
//                            Spacer(modifier = Modifier.height(12.dp))
//                            Text(
//                                month,
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 18.sp,
//                                modifier = Modifier.padding(bottom = 8.dp)
//                            )
//                        }
//                        items(transactions) { transaction ->
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .background(Color.White, RoundedCornerShape(8.dp))
//                                    .padding(12.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Row(verticalAlignment = Alignment.CenterVertically) {
//                                    Box(
//                                        modifier = Modifier
//                                            .size(40.dp)
//                                            .clip(RoundedCornerShape(12.dp))
//                                            .background(Color(0xFF4C9CDA)),
//                                        contentAlignment = Alignment.Center
//                                    ) {
//                                        Icon(
//                                            imageVector = transaction.icon,
//                                            contentDescription = transaction.description,
//                                            tint = Color.White
//                                        )
//                                    }
//                                    Spacer(modifier = Modifier.width(12.dp))
//                                    Column(modifier = Modifier.weight(1f)) {
//                                        Text(transaction.description, fontWeight = FontWeight.Bold)
//                                        Text(
//                                            "${transaction.time} - ${transaction.date}",
//                                            color = Color.Gray,
//                                            fontSize = 12.sp
//                                        )
//                                    }
//
//                                    Spacer(
//                                        modifier = Modifier
//                                            .width(1.dp)
//                                            .fillMaxHeight(0.7f)
//                                            .background(Color.Black)
//                                    )
//
//                                    Text(
//                                        text = transaction.category,
//                                        color = Color.Black,
//                                        fontSize = 12.sp,
//                                        modifier = Modifier.padding(start = 8.dp)
//                                    )
//
//                                    Spacer(
//                                        modifier = Modifier
//                                            .width(1.dp)
//                                            .fillMaxHeight(0.7f)
//                                            .background(Color.Black)
//                                            .padding(start = 8.dp)
//                                    )
//
//                                    Text(
//                                        text = "${if (transaction.amount < 0) "-" else ""}$${
//                                            String.format("%.2f", Math.abs(transaction.amount))
//                                        }",
//                                        fontWeight = FontWeight.Bold,
//                                        color = if (transaction.amount > 0) Color(0xFF4CAF50) else Color(0xFFF44336),
//                                        textAlign = TextAlign.End,
//                                        modifier = Modifier.padding(start = 8.dp)
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    // Hiển thị dialog thêm income
//    if (showAddIncomeDialog) {
//        AddIncomeDialog(
//            onDismiss = { showAddIncomeDialog = false },
//            onSave = { transaction ->
//                Log.d("TransactionScreen", "Saving transaction: $transaction")
//                viewModel.addTransaction(transaction)
//                showAddIncomeDialog = false
//            }
//        )
//    }
//}
//
//@Composable
//fun AddIncomeDialog(
//    onDismiss: () -> Unit,
//    onSave: (Transaction) -> Unit
//) {
//    var title by remember { mutableStateOf("") }
//    var amount by remember { mutableStateOf("") }
//    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())) }
//    var period by remember { mutableStateOf("one-time") }
//    var periodExpanded by remember { mutableStateOf(false) }
//
//    Dialog(onDismissRequest = onDismiss) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Text(
//                    "Add Income",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF4CAF50)
//                )
//
//                OutlinedTextField(
//                    value = title,
//                    onValueChange = { title = it },
//                    label = { Text("Title") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                OutlinedTextField(
//                    value = amount,
//                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amount = it },
//                    label = { Text("Amount ($)") },
//                    modifier = Modifier.fillMaxWidth(),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
//                )
//
//                OutlinedTextField(
//                    value = date,
//                    onValueChange = { date = it },
//                    label = { Text("Date (yyyy-MM-dd HH:mm:ss)") },
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = false // Date picker could be added
//                )
//
//                Box {
//                    OutlinedTextField(
//                        value = period,
//                        onValueChange = {},
//                        label = { Text("Period") },
//                        modifier = Modifier.fillMaxWidth(),
//                        readOnly = true,
//                        trailingIcon = {
//                            IconButton(onClick = { periodExpanded = !periodExpanded }) {
//                                Icon(
//                                    imageVector = if (periodExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
//                                    contentDescription = "Toggle period dropdown"
//                                )
//                            }
//                        }
//                    )
//                    DropdownMenu(
//                        expanded = periodExpanded,
//                        onDismissRequest = { periodExpanded = false },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        listOf("one-time", "monthly", "weekly").forEach { option ->
//                            DropdownMenuItem(
//                                onClick = {
//                                    period = option
//                                    periodExpanded = false
//                                }
//                            ) {
//                                Text(option)
//                            }
//
//                        }
//                    }
//                }
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    TextButton(onClick = onDismiss) {
//                        Text("Cancel")
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(
//                        onClick = {
//                            if (title.isNotBlank() && amount.isNotBlank() && amount.toDoubleOrNull() != null) {
//                                onSave(
//                                    Transaction(
//                                        id = "", // Explicitly set id to empty
//                                        title = title,
//                                        amount = amount.toDouble(),
//                                        type = "income",
//                                        date = date,
//                                        period = period,
//                                        isPositive = true
//                                    )
//                                )
//                            }
//                        },
//                        enabled = title.isNotBlank() && amount.isNotBlank() && amount.toDoubleOrNull() != null
//                    ) {
//                        Text("Save")
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Hàm hỗ trợ parse ngày
//private fun parseDate(dateString: String): Date {
//    return try {
//        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateString)
//            ?: Date()
//    } catch (e: Exception) {
//        Date()
//    }
//}