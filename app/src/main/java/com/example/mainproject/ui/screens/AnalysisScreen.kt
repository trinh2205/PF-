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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.CalendarBar
import com.example.mainproject.ui.components.NavigationItem

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
fun AnalysisContent(onCalendarClick: () -> Unit, navController: NavController, currentRoute: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
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
                    Icons.Default.ArrowBack,
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
                    modifier = Modifier.clickable { onCalendarClick() } // Bấm vào icon Calendar
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
                        text = "$7,783.00",
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
                        text = "-$1,187.40",
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
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                ) {
                    Text(
                        "30%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Text(
                    "$20,000.00",
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
                    checked = true,
                    onCheckedChange = {}
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "30% Of Your Expenses, Looks Good.",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                AnalysissBackgroundBar(
                    onCalendarClick = onCalendarClick // Truyền sự kiện click tiếp xuống
                )
            }

            Box(modifier = Modifier.background(Color.White)) {
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
        }
    }
}

@Composable
fun AnalysissBackgroundBar(onCalendarClick: () -> Unit) {
    val tabs = listOf("Daily", "Weekly", "Monthly", "Year")
    var selectedTab by remember { mutableStateOf("Year") }
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
            // Tab switcher
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
                            .clickable { selectedTab = tab }
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

            Spacer(modifier = Modifier.height(20.dp))

            // Income & Expense chart section
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
                            text = "Income & Expenses",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF052224)
                        )
                        Row {
                            IconButton(onClick = { }) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF052224))
                            }
                            IconButton(onClick = { onCalendarClick() }) {
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
                        BarChart(selectedTab = selectedTab)

                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Income & Expense Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CallReceived, contentDescription = null, tint = Color(0xFF00B894))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Income", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("$4,120.00", color = Color.Black, fontSize = 16.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CallMade, contentDescription = null, tint = Color(0xFF0984E3))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Expense", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("$1,187.40", color = Color(0xFF0984E3), fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // My Target
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

                // Target Item - ví dụ một mục tiêu, có thể lặp nếu cần
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFFFFF))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("🎯 Save for Vacation", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFE0E0E0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f) // 60% hoàn thành
                                    .fillMaxHeight()
                                    .background(Color(0xFF3498DB))
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("60% completed", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
@Composable
fun BarChart(selectedTab: String) {
    val dailyData1 = listOf(3000f, 1000f, 2000f, 500f, 4000f, 700f, 1500f)
    val dailyData2 = listOf(6000f, 200f, 5000f, 3500f, 10000f, 1000f, 6500f)

    val weeklyData1 = listOf(6000f, 3000f, 4000f, 2000f)
    val weeklyData2 = listOf(10000f, 5000f, 8000f, 7000f)

    val monthlyData1 = listOf(8000f, 10000f, 6000f, 5000f, 7000f, 9000f, 11000f)
    val monthlyData2 = listOf(12000f, 8000f, 7000f, 9500f, 10000f, 8500f, 13000f)

    val yearlyData1 = listOf(60000f, 80000f, 90000f, 70000f, 100000f, 95000f)
    val yearlyData2 = listOf(70000f, 85000f, 95000f, 80000f, 110000f, 97000f)

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weeks = listOf("1st", "2nd", "3rd", "4th Week")
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul")
    val years = listOf("2020", "2021", "2022", "2023", "2024", "2025")

    val chartData1 = when (selectedTab) {
        "Weekly" -> weeklyData1
        "Monthly" -> monthlyData1
        "Year" -> yearlyData1
        else -> dailyData1
    }

    val chartData2 = when (selectedTab) {
        "Weekly" -> weeklyData2
        "Monthly" -> monthlyData2
        "Year" -> yearlyData2
        else -> dailyData2
    }

    val labels = when (selectedTab) {
        "Weekly" -> weeks
        "Monthly" -> months
        "Year" -> years
        else -> days
    }

    val maxValue = (chartData1 + chartData2).maxOrNull()?.coerceAtLeast(1f) ?: 1f

    val barColors = listOf(Color(0xFF00C2A8), Color(0xFF007BFF))
    val chartHeight = 120.dp
    val barWidth = 6.dp
    val spaceBetweenBars = 4.dp
    val barCornerRadius = 50.dp

    // 🔁 Tự động điều chỉnh label dựa vào chế độ
    val labelValues = if (selectedTab == "Year") listOf("60k", "80k", "100k") else listOf("5k", "10k", "15k")
    val textColor = Color(0xFF4AA8FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6FCE9))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(modifier = Modifier.height(chartHeight)) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                labelValues.reversed().forEach {
                    Text(text = it, fontSize = 12.sp, color = textColor)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasHeight = size.height
                    val stepY = canvasHeight / 3
                    repeat(3) { i ->
                        val y = stepY * (i + 1)
                        drawLine(
                            color = textColor,
                            start = Offset(0f, canvasHeight - y),
                            end = Offset(size.width, canvasHeight - y),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 4.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    labels.indices.forEach { i ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(spaceBetweenBars)
                            ) {
                                listOf(chartData1[i], chartData2[i]).forEachIndexed { j, value ->
                                    val heightRatio = value / maxValue
                                    val animatedHeight by animateDpAsState(
                                        targetValue = chartHeight * heightRatio,
                                        animationSpec = tween(durationMillis = 600)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(barWidth)
                                            .height(animatedHeight)
                                            .clip(RoundedCornerShape(barCornerRadius))
                                            .background(barColors[j])
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .padding(start = 28.dp)) {
            drawLine(
                color = Color.Black,
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 2f
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 28.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}