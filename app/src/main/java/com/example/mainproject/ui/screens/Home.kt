package com.example.mainproject.ui.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.data.model.TransactionBE
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.NavigationItem
import com.example.mainproject.viewModel.AppViewModel
import com.example.mainproject.viewModel.AppViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.*
import androidx.compose.foundation.lazy.items
import com.example.mainproject.NAVIGATION.Routes
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun Home(
    navController: NavHostController,
    auth: FirebaseAuth
) {
    val appViewModel: AppViewModel = viewModel(
        factory = AppViewModelFactory(auth)
    )
    val totalBalance by appViewModel.totalBalance.collectAsState()
    val currentUserInfo by appViewModel.currentUser.collectAsState()
    val totalExpenseState by appViewModel.totalExpense.collectAsState()
    val totalBudget by appViewModel.totalBudget.collectAsState()
    val dailyTransactions by appViewModel.dailyTransactions.collectAsState()
    val weeklyTransactions by appViewModel.weeklyTransactions.collectAsState()
    val monthlyTransactions by appViewModel.monthlyTransactions.collectAsState()
    var selectedFilter by remember { mutableStateOf("Weekly") }
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

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
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ){
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF3498DB)).padding(40.dp)
                ){
                    Column{
                        Text(text = "Hi, Welcome Back", fontSize = 20.sp, color = Color.Black)
                        Text(text = "${currentUserInfo?.name ?: ""}", fontSize = 16.sp, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.Black,
                        modifier = Modifier.clickable {
                            navController.navigate(route = Routes.NOTIFICATION)
                        }
                    )
                }

            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(route = Routes.CHATBOT)
                },
                backgroundColor = Color(0xFF3498DB),
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp, // Elevation khi button ở trạng thái bình thường
                    pressedElevation = 12.dp, // Elevation khi button được nhấn
                )
            ) {
                Icon(Icons.Filled.Chat, "Chatbot")
            }
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF3498DB))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
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
                            text = "Total Balance",
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
                            text = "Total Expense",
                            color = Color.Black.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = formatter.format(totalExpenseState),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFFFF3B30)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                BudgetProgressBar(
                    budget = totalBalance,
                    expense = totalExpenseState
                )
                Spacer(modifier = Modifier.height(5.dp))
//                VerificationStatus(isVerified = currentUserInfo?.isVerified ?: false)
                Spacer(modifier = Modifier.height(16.dp))
                TransactionSection(
                    dailyTransactions = dailyTransactions,
                    weeklyTransactions = weeklyTransactions,
                    monthlyTransactions = monthlyTransactions,
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it }
                )
            }
        }
    }
}

@Composable
fun BudgetProgressBar(budget: Double, expense: Double) {
    val progress = if (budget > 0) (expense / budget).coerceIn(0.0, 1.0) else 0.0
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .padding(horizontal = 40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE6FFF9))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.toFloat())
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Text(
            text = "$${String.format("%.2f", budget)}",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )
    }
}

//@Composable
//fun VerificationStatus(isVerified: Boolean) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 40.dp),
//        horizontalArrangement = Arrangement.Start,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Checkbox(
//            checked = isVerified,
//            onCheckedChange = { /* Handle verification toggle if needed */ },
//            enabled = false
//        )
//        Spacer(modifier = Modifier.width(8.dp))
//        Text(
//            text = if (isVerified) "Email đã xác minh" else "Email chưa xác minh",
//            color = Color.Black,
//            fontSize = 14.sp
//        )
//    }
//}

@Composable
fun TransactionSection(
    dailyTransactions: List<TransactionBE>,
    weeklyTransactions: List<TransactionBE>,
    monthlyTransactions: List<TransactionBE>,
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val transactionsToShow = when (selectedFilter) {
        "Daily" -> dailyTransactions
        "Weekly" -> weeklyTransactions
        "Monthly" -> monthlyTransactions
        else -> weeklyTransactions // Giá trị mặc định
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(Color(0xFFF4FFF9))
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Column {
            FinancialCard()
            Spacer(modifier = Modifier.height(16.dp))
            TimeFilterToggle(
                selected = selectedFilter,
                onSelected = onFilterChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransactionList(
                transactionsList = transactionsToShow,
                modifier = Modifier.height(300.dp) // Đặt chiều cao cố định cho TransactionList
            )
        }
    }
}

@Composable
fun TransactionList(transactionsList: List<TransactionBE>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        if (transactionsList.isEmpty()) {
            Text(
                "Không có giao dịch nào trong khoảng thời gian này.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn {
                items(transactionsList) { transaction ->
                    Log.d("TransactionList", "Rendering transaction: $transaction")
                    TransactionItem(transaction = transaction)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun TimeFilterToggle(selected: String, onSelected: (String) -> Unit) {
    val options = listOf("Daily", "Weekly", "Monthly")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFDFF7E2))
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEach { option ->
                val isSelected = selected == option
                Text(
                    text = option,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF3498DB) else Color.Transparent)
                        .clickable { onSelected(option) }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun FinancialCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF3498DB))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Color.White,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = Color(0xFF007BFF),
                            startAngle = -90f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Savings\nOn Goals",
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
            Divider(
                color = Color.White,
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp)
            )
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Revenue Last Week", fontSize = 12.sp, color = Color.Black)
                        Text(
                            "$4,000.00", // Dữ liệu cũ - sẽ được bỏ qua
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Food Last Week", fontSize = 12.sp, color = Color.Black)
                        Text(
                            "-$100.00", // Dữ liệu cũ - sẽ được bỏ qua
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionBE) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3498DB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type == "expense") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = transaction.title,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = transaction.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = LocalDate.parse(transaction.date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    fontSize = 12.sp,
                    color = Color(0xFF1D71B8)
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (transaction.type == "expense") "Chi phí" else "Thu nhập",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "${if (transaction.type == "expense") "-" else ""}${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(transaction.amount)}",
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == "expense") Color.Red else Color(0xFF00C853)
            )
        }
    }
}