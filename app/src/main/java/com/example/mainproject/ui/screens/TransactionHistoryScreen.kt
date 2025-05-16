package com.example.mainproject.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.NAVIGATION.Routes
import com.example.mainproject.data.model.Income
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.CustomHeader
import com.example.mainproject.ui.components.NavigationItem
import com.example.mainproject.ui.components.miniBox
import com.example.mainproject.viewModel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

data class TransactionItem(
    val month: String,
    val date: String,
    val time: String,
    val icon: ImageVector,
    val description: String,
    val category: String,
    val amount: Double
)

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TransactionScreen(
    navController: NavController,
    viewModel: TransactionViewModel = viewModel()
) {
    // Thu thập dữ liệu từ ViewModel
    val transactions by viewModel.filteredTransactions.collectAsState()
    val isLoading by viewModel.isLoadingTransactions.collectAsState()
    val listCategories by viewModel.listCategories.collectAsState()

    // Chuyển đổi UnifiedTransaction thành TransactionItem và tính tổng balance
    val transactionItems by remember(transactions, listCategories) {
        derivedStateOf {
            val items = transactions.map { transaction ->
                val date = parseDate(transaction.date)
                val categoryName = viewModel.getCategoryNameById(transaction.categoryId) ?: "Unknown"
                TransactionItem(
                    month = SimpleDateFormat("MMMM", Locale.getDefault()).format(date),
                    date = SimpleDateFormat("dd", Locale.getDefault()).format(date),
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date),
                    icon = if (transaction.type == "income") Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    description = transaction.title,
                    category = categoryName,
                    amount = if (transaction.type == "income") transaction.amount else -transaction.amount
                )
            }
            items
        }
    }
    val totalBalance by remember(transactionItems) {
        derivedStateOf {
            transactionItems.sumOf { it.amount }
        }
    }

    var showAddIncomeDialog by remember { mutableStateOf(false) }

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
        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { showAddIncomeDialog = true },
//                backgroundColor = Color(0xFF4CAF50),
//                contentColor = Color.White
//            ) {
//                Icon(Icons.Filled.Add, contentDescription = "Add Income")
//            }
//        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF4C9CDA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                CustomHeader(
                    title = "Transaction",
                    onBackClick = { navController.popBackStack() },
                    backgroundColor = Color(0xFF4C9CDA),
                    contentColor = Color.White,
                    onNotificationClick = {
                        navController.navigate(Routes.NOTIFICATION)
                    }
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
                        onClick = { viewModel.setTransactionType("all") },
                        icon = null,
                        title = "Total Balance",
                        money = totalBalance,
                        colorBG = if (viewModel.filteredTransactions.value == transactions) Color(0xFF42A5F5) else Color(0xFFFFFFFF),
                        colorText = if (viewModel.filteredTransactions.value == transactions) Color.White else Color.Black,
                        fillWidth = 0.dp,
                        fillHeight = 72.dp,
                        modifier = Modifier.weight(1f),
                        isActive = viewModel.filteredTransactions.value == transactions,
                        activeIconColor = Color.White,
                        activeTextColor = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    miniBox(
                        onClick = { viewModel.setTransactionType("income") },
                        icon = Icons.Filled.ArrowUpward,
                        title = "Income",
                        money = transactionItems.filter { it.amount > 0 }.sumOf { it.amount },
                        colorBG = if (viewModel.filteredTransactions.value == transactions.filter { it.type == "income" }) Color(0xFF81C784) else Color(0xFFF4FFF9),
                        colorText = if (viewModel.filteredTransactions.value == transactions.filter { it.type == "income" }) Color.White else Color.Black,
                        fillWidth = 0.dp,
                        fillHeight = 100.dp,
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable { viewModel.setTransactionType("income") },
                        isActive = viewModel.filteredTransactions.value == transactions.filter { it.type == "income" },
                        activeIconColor = Color.White,
                        activeTextColor = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    miniBox(
                        onClick = { viewModel.setTransactionType("expense") },
                        icon = Icons.Filled.ArrowDownward,
                        title = "Expense",
                        money = transactionItems.filter { it.amount < 0 }.sumOf { -it.amount },
                        colorBG = if (viewModel.filteredTransactions.value == transactions.filter { it.type == "expense" }) Color(0xFFF06292) else Color(0xFFF4FFF9),
                        colorText = if (viewModel.filteredTransactions.value == transactions.filter { it.type == "expense" }) Color.White else Color(0xFF4C9CDA),
                        fillWidth = 0.dp,
                        fillHeight = 100.dp,
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable { viewModel.setTransactionType("expense") },
                        isActive = viewModel.filteredTransactions.value == transactions.filter { it.type == "expense" },
                        activeIconColor = Color.White,
                        activeTextColor = Color.White
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
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 40.dp)) {
                        val groupedTransactions = transactionItems.groupBy { it.month }
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
                                            Icon(
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
                                                .fillMaxHeight(0.7f)
                                                .background(Color.Black)
                                        )

                                        Text(
                                            text = transaction.category,
                                            color = Color.Black,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )

                                        Spacer(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .fillMaxHeight(0.7f)
                                                .background(Color.Black)
                                                .padding(start = 8.dp)
                                        )

                                        Text(
                                            text = "${if (transaction.amount < 0) "-" else ""}$${
                                                String.format("%.2f", Math.abs(transaction.amount))
                                            }",
                                            fontWeight = FontWeight.Bold,
                                            color = if (transaction.amount > 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.padding(start = 8.dp)
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

    // Hiển thị dialog thêm income
    if (showAddIncomeDialog) {
        AddIncomeDialog(
            viewModel = viewModel,
            onDismiss = { showAddIncomeDialog = false }
        )
    }
}

@Composable
fun AddIncomeDialog(
    viewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var categoryId by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val listCategories by viewModel.listCategories.collectAsState()
    val categoryNames = listCategories.values.map { it.name }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Add Income",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amount = it },
                    label = { Text("Amount ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false // Date picker could be added
                )

                Box {
                    OutlinedTextField(
                        value = listCategories.values.find { it.id == categoryId }?.name ?: "Select Category",
                        onValueChange = {},
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { categoryExpanded = !categoryExpanded }) {
                                Icon(
                                    imageVector = if (categoryExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                    contentDescription = "Toggle category dropdown"
                                )
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categoryNames.forEach { name ->
                            DropdownMenuItem(
                                onClick = {
                                    categoryId = listCategories.values.find { it.name == name }?.id ?: ""
                                    categoryExpanded = false
                                }
                            ) {
                                Text(name)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && amount.isNotBlank() && amount.toDoubleOrNull() != null && categoryId.isNotBlank()) {
                                viewModel.addIncome(
                                    categoryId = categoryId,
                                    newIncome = Income(
                                        id = "",
                                        title = title,
                                        amount = amount.toDouble(),
                                        date = date,
                                        categoryId = categoryId,
                                        message = "Income for '$title'"
                                    )
                                )
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() && amount.isNotBlank() && amount.toDoubleOrNull() != null && categoryId.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

// Hàm hỗ trợ parse ngày
private fun parseDate(dateString: String): Date {
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
            ?: Date()
    } catch (e: Exception) {
        Log.e("TransactionScreen", "Error parsing date: $dateString", e)
        Date()
    }
}