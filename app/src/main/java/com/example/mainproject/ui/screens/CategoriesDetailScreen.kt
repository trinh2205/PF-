package com.example.mainproject.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.viewModel.TransactionViewModel
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.data.model.Category
import com.example.mainproject.data.model.Expense
import com.example.mainproject.R
import androidx.compose.foundation.text.KeyboardOptions
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

@Composable
fun CategoryDetailScreen(
    navController: NavController,
    ListCategoryId: String,
    viewModel: TransactionViewModel = viewModel(),
    onBack: () -> Unit,
    onAddExpenseClick: () -> Unit
) {
    val transactionsBE by viewModel.transactionsBE.collectAsState()
    val categoriesMap by viewModel.categories.collectAsState()
    val currentCategory = remember(categoriesMap) {
        categoriesMap[ListCategoryId]
    }
    val categoryExpenses = remember(transactionsBE) {
        transactionsBE.values.filter { it.categoryId == ListCategoryId && it.type == "expense" }
            .map {
                Expense(
                    id = it.id,
                    title = it.title,
                    amount = it.amount * -1, // Chuyển lại thành số dương để hiển thị
                    date = it.date,
                    categoryId = it.categoryId
                )
            }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val totalBalance by viewModel.totalBalance.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    var expenseAnalysisChecked by remember { mutableStateOf(true) }
    val iconVector = categoryIcons[currentCategory?.iconId] ?: Icons.Filled.Help
    var showDialog by remember { mutableStateOf(false) }
    var newExpenseTitle by remember { mutableStateOf("") }
    var newExpenseAmount by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }
    var selectedCategoryId by remember { mutableStateOf(ListCategoryId) } // Mặc định chọn category hiện tại

    var newlyCreatedCategoryId by remember { mutableStateOf<String?>(null) }
    var categoryCreated by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Expense") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newExpenseTitle,
                        onValueChange = { newExpenseTitle = it },
                        label = { Text("Title") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newExpenseAmount,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                                newExpenseAmount = input
                                amountError = null
                            } else {
                                amountError = "Please enter a valid number"
                            }
                        },
                        label = { Text("Amount") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        isError = amountError != null,
                        supportingText = {
                            amountError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = newExpenseAmount.toDoubleOrNull() ?: 0.0
                        if (newExpenseTitle.isNotBlank() && amount > 0 && amountError == null) {
                            val newExpense = Expense(
                                title = newExpenseTitle,
                                amount = amount,
                                date = LocalDate.now().toString(),
                                categoryId = selectedCategoryId
                            )

                            Log.d("AddExpenseUI", "Adding expense to Category ID: $selectedCategoryId")
                            viewModel.addExpense(ListCategoryId, selectedCategoryId, newExpense)
                            newExpenseTitle = ""
                            newExpenseAmount = ""
                            amountError = null
                            showDialog = false
                        } else {
                            // Xử lý lỗi input
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = currentRoute ?: com.example.mainproject.ui.components.NavigationItem.DefaultItems.first().route,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF3498DB))
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        stringResource(R.string.total_balance),
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = formatter.format(totalBalance),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(Color.White)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        stringResource(R.string.total_expense),
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${formatter.format(totalExpense)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFFFF3B30)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE6FFF9))
            ) {
                val expensePercentage = if (totalBalance > 0) (totalExpense / totalBalance * 100).toInt() else 0
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((expensePercentage / 100f).coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                ) {
                    Text(
                        "$expensePercentage%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Text(
                    formatter.format(totalBalance),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = expenseAnalysisChecked,
                    onCheckedChange = { expenseAnalysisChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.expense_analysis),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color(0xFFF4FFF9))
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                if (categoryExpenses.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No expenses found.", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text("Add expense")
                        }
                    }
                } else {
                    Column {
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text("Add expense")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(categoryExpenses) { expense ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF3498DB)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = iconVector,
                                                    contentDescription = expense.title,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(expense.title, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = expense.date,
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "-${formatter.format(expense.amount)}",
                                                color = Color(0xFFFF3B30),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
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
}