package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.R
import com.example.mainproject.data.model.Expense
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.viewModel.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

val categoryIcons = mapOf(
    "Ăn uống" to Icons.Filled.Restaurant,
    "Công nghệ" to Icons.Filled.Android,
    "Văn học" to Icons.Filled.Book,
    "Thời trang" to Icons.Filled.Checkroom,
    "Đời sống" to Icons.Filled.Home,
    "Giải trí" to Icons.Filled.PlayArrow,
    "Khám phá" to Icons.Filled.Explore,
    "Học tập" to Icons.Filled.School,
    "Chăm sóc bản thân" to Icons.Filled.Spa
)

@Composable
fun CategoryDetailScreen(
    navController: NavController,
    listCategoryId: String,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.provideFactory(auth = FirebaseAuth.getInstance())),
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val transactionsBE by viewModel.allTransactions.collectAsState()
    val listCategories by viewModel.listCategories.collectAsState()
    val currentCategory = listCategories.find { it.id == listCategoryId }
    val categoryExpenses = transactionsBE
        .filter { it.categoryId == listCategoryId && it.type == "expense" }
        .map {
            Expense(
                id = it.id,
                title = it.title,
                amount = it.amount, // Amount is positive in Firebase
                date = it.date,
                categoryId = it.categoryId,
                message = it.message
            )
        }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val totalBalance by viewModel.totalBalance.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    var expenseAnalysisChecked by remember { mutableStateOf(true) }
    val iconVector = categoryIcons[currentCategory?.icon] ?: Icons.Filled.Help
    var showDialog by remember { mutableStateOf(false) }

    // Dialog state
    var newExpenseTitle by remember { mutableStateOf("") }
    var newExpenseAmount by remember { mutableStateOf("") }
    var newExpenseMessage by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Thêm chi phí mới") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newExpenseTitle,
                        onValueChange = { newExpenseTitle = it },
                        label = { Text("Tiêu đề") },
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
                                amountError = "Vui lòng nhập số hợp lệ"
                            }
                        },
                        label = { Text("Số tiền") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = amountError != null,
                        supportingText = {
                            amountError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newExpenseMessage,
                        onValueChange = { newExpenseMessage = it },
                        label = { Text("Ghi chú") },
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = newExpenseAmount.toDoubleOrNull() ?: 0.0
                        if (newExpenseTitle.isNotBlank() && amount > 0 && amountError == null) {
                            val newExpense = Expense(
                                id = UUID.randomUUID().toString(),
                                categoryId = listCategoryId,
                                title = newExpenseTitle,
                                amount = amount,
                                date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                message = newExpenseMessage
                            )
                            viewModel.addExpense(listCategoryId, newExpense)
                            newExpenseTitle = ""
                            newExpenseAmount = ""
                            newExpenseMessage = ""
                            amountError = null
                            showDialog = false
                        }
                    },
                    enabled = newExpenseTitle.isNotBlank() && newExpenseAmount.isNotBlank() && amountError == null
                ) {
                    Text("Thêm")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = currentRoute ?: "default_route", // Replace with actual default route
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
                        text = formatter.format(totalExpense),
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
                        Text("Chưa có chi phí nào.", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text("Thêm chi phí")
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
                            Text("Thêm chi phí")
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
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
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
                                                if (expense.message.isNotBlank()) {
                                                    Text(
                                                        text = expense.message,
                                                        fontSize = 12.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = "-${formatter.format(expense.amount)}",
                                                color = Color(0xFFFF3B30),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteExpense(expense.id)
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Xóa chi phí",
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(16.dp)
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
}