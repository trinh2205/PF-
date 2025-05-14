package com.example.mainproject.ui.screens

//noinspection SuspiciousImport
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.data.repository.AnalysisRepository
import com.example.mainproject.data.repository.IncomeExpenseSummary
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.CalendarBar
import com.example.mainproject.ui.components.NavigationItem
import com.example.mainproject.ui.viewmodel.AnalysisViewModel
import com.example.mainproject.ui.viewmodel.ChartBarData
import com.example.mainproject.viewModel.AnalysisViewModelFactory
import com.example.mainproject.viewModel.AppViewModel
import com.example.mainproject.viewModel.AppViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

public val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

@Composable
fun AnalysisScreen(navController: NavController) {
    var isCalendarVisible by remember { mutableStateOf(false) } // Thêm state
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (isCalendarVisible) {
        // Nếu click vào icon Calendar -> chuyển sang CalendarBar()
        CalendarBar(
            onBackClick = { isCalendarVisible = false } // Khi nhấn quay lại
        )
    } else {
        // Giao diện mặc định Analysis
        AnalysisContent(
            onCalendarClick = { isCalendarVisible = true },
            navController = navController,
            currentRoute = currentRoute
        )
    }
}
@Composable
fun AnalysisContent(
    onCalendarClick: () -> Unit,
    navController: NavController,
    analysisRepository: AnalysisRepository = remember { AnalysisRepository() },
    viewModel: AnalysisViewModel = viewModel(factory = AnalysisViewModelFactory(analysisRepository)),
    currentRoute: String?,
    AppViewModel: AppViewModel = viewModel(factory = AppViewModelFactory(auth))
) {
    val totalBalance by AppViewModel.totalBalance.collectAsState()
    val totalExpense by AppViewModel.totalExpense.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

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
                .fillMaxSize()
                .background(Color(0xFFFFFFFF))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF3498DB))
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Text(
                        text = "Analysis",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.clickable(onClick = onCalendarClick)
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Total Balance", color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
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
                        Text("Total Expense", color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text(
                            text = formatter.format(totalExpense),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFFFF3B30)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Loại bỏ BudgetProgressBar và Checkbox vì không liên quan đến biểu đồ chính

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f)) {
                    AnalysisBackgroundBar(
                        onCalendarClick = onCalendarClick,
                        viewModel = viewModel,
                    )
                }

                Box(modifier = Modifier.background(Color.White)) {
                    // Phần này có thể chứa các thông tin hoặc thống kê khác nếu cần
                }
            }
        }
    }
}

@Composable
fun BudgetProgressBar(progress: Float?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE6FFF9))
    ) {
        progress?.let {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(it)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
            ) {
                Text(
                    text = "${(it * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun AnalysisBackgroundBar(onCalendarClick: () -> Unit, viewModel: AnalysisViewModel) {
    val tabs = remember { listOf("Daily", "Weekly", "Monthly", "Year") }
    val selectedTab by viewModel.selectedChartTab.collectAsState() // Lấy selectedTab từ ViewModel

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(Color(0xFFF4FFF9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            TabSwitcher(tabs = tabs, selectedTab = selectedTab) {
                viewModel.setSelectedChartTab(it) // Gọi hàm trong ViewModel khi tab thay đổi
            }

            Spacer(modifier = Modifier.height(20.dp))

            IncomeExpenseChartSection(
                selectedTab = selectedTab, // Truyền selectedTab xuống
                onCalendarClick = onCalendarClick,
                chartData = viewModel.chartData.collectAsState().value // Lấy chartData từ ViewModel
            )

            Spacer(modifier = Modifier.height(16.dp))

            MyTargetsSection(viewModel = viewModel)
        }
    }
}

@Composable
fun TabSwitcher(tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE6FFF9))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { tab ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selectedTab == tab) Color(0xFF3498DB) else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab,
                    color = if (selectedTab == tab) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun IncomeExpenseChartSection(
    selectedTab: String, // Nhận selectedTab làm tham số
    onCalendarClick: () -> Unit,
    chartData: List<ChartBarData> // Cập nhật kiểu dữ liệu tại đây
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFD5F3E9))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Income & Expenses", // Bạn có thể thay đổi tiêu đề dựa trên selectedTab nếu muốn
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF052224)
                )
                Row {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF052224))
                    }
                    IconButton(onClick = onCalendarClick) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendar", tint = Color(0xFF052224))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                BarChart(selectedTab = selectedTab, chartData = chartData) // Truyền selectedTab xuống BarChart
            }
        }
    }
}

@Composable
fun BarChart(selectedTab: String, chartData: List<ChartBarData>) {
    val barColorExpense = Color.Red
    val barColorIncome = Color.Green
    val spacing = 10.dp
    val maxY = remember(chartData) {
        val maxVal = chartData.maxOfOrNull { maxOf(it.expense, it.income) } ?: 0.0f
        maxVal.coerceAtLeast(1.0f) // Ép kiểu về Float
    }
    val density = LocalDensity.current

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp) // Sử dụng padding ngang riêng
            .padding(bottom = 24.dp)    // Sử dụng padding dưới riêng
    ) {
        val innerWidth = size.width - (spacing * (chartData.size + 1)).toPx()
        val barWidth = innerWidth / chartData.size

        chartData.forEachIndexed { index, data ->
            val xOffset = (index + 1) * spacing.toPx() + index * barWidth

            // Tính toán chiều cao cột chi tiêu
            val expenseHeight = (data.expense / maxY) * size.height

            // Vẽ cột chi tiêu
            drawRect(
                color = barColorExpense,
                topLeft = Offset(x = xOffset.toFloat(), y = (size.height - expenseHeight).toFloat()),
                size = Size(width = (barWidth / 2).toFloat(), height = expenseHeight.toFloat())
            )

            // Tính toán chiều cao cột thu nhập
            val incomeHeight = (data.income / maxY) * size.height

            // Vẽ cột thu nhập
            drawRect(
                color = barColorIncome,
                topLeft = Offset(x = (xOffset + barWidth / 2).toFloat(), y = (size.height - incomeHeight).toFloat()),
                size = Size(width = (barWidth / 2).toFloat(), height = incomeHeight.toFloat())
            )

            // Vẽ label
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = with(density) { 12.dp.toPx() }
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    data.label,
                    xOffset + barWidth / 2,
                    size.height + with(density) { 16.dp.toPx() },
                    textPaint
                )
            }
        }
    }
}

private fun getMonthLabels(count: Int): List<String> {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
    return (0 until count).map { months[(currentMonth - count + 1 + it + 12) % 12] }
}

private fun getDayLabels(count: Int): List<String> {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val calendar = java.util.Calendar.getInstance()
    val currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK) // Sunday=1, Monday=2, ... Saturday=7
    return (0 until count).map { days[(currentDayOfWeek - count + it + 7) % 7] }
}
@Composable
fun IncomeExpenseSummaryRow(viewModel: AnalysisViewModel) {
    val incomeExpenseSummary by viewModel.incomeExpenseSummary.collectAsState(initial = IncomeExpenseSummary(0.0, 0.0))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.CallReceived, contentDescription = "Income", tint = Color(0xFF00B894))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Income", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(incomeExpenseSummary.totalIncome), color = Color.Black, fontSize = 16.sp)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.CallMade, contentDescription = "Expense", tint = Color(0xFF0984E3))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Expense", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(incomeExpenseSummary.totalExpense), color = Color(0xFF0984E3), fontSize = 16.sp)
        }
    }
}

@Composable
fun MyTargetsSection(viewModel: AnalysisViewModel) {
    val financialGoals by viewModel.financialGoals.collectAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(
            text = "My Targets",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF052224)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (financialGoals.isEmpty()) {
            Text("No financial goals set yet.", color = Color.Gray)
        } else {
            financialGoals.forEach { goal ->
                TargetItem(goal = goal)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TargetItem(goal: com.example.mainproject.data.model.FinancialGoal) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFFFF))
            .padding(16.dp)
    ) {
        Column {
            Text(goal.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(Color(0xFF3498DB))
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("${(goal.currentAmount / goal.targetAmount * 100).toInt()}% completed", fontSize = 12.sp, color = Color.Gray)
        }
    }
}