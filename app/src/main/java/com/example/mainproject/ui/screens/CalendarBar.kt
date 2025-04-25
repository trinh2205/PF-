package com.example.mainproject.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import com.example.mainproject.ui.components.BottomNavigationBar

import java.time.LocalDate
import java.time.Month
import java.time.YearMonth



@Composable
fun CalendarBar(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3498DB))
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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .clickable { onBackClick() } // <<< Thêm sự kiện click cho nút Back
                )
                Text(
                    text = "Calendar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Calendar content + bottom bar
        Box(modifier = Modifier.weight(1f)) {
            CalendarBackgroundBar()
        }

        BottomNavigationBar(
            selectedItem = TODO(),
            onItemClick = TODO(),
            items = TODO()
        )
    }
}


@Composable
fun CalendarBackgroundBar() {
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
            Spacer(modifier = Modifier.height(24.dp))
            CalendarView()

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs (Spends, Categories)
            // Tabs (Spends, Categories)
            var selectedTab by remember { mutableStateOf("Spends") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterHorizontally) // 👈 tăng khoảng cách ở đây
            ) {
                // Spends Tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (selectedTab == "Spends") Color(0xFF3498DB) else Color(0xFFE6FFF9))
                        .clickable { selectedTab = "Spends" }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Spends",
                        color = if (selectedTab == "Spends") Color.White else Color(0xFF052224),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Categories Tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (selectedTab == "Categories") Color(0xFF3498DB) else Color(0xFFE6FFF9))
                        .clickable { selectedTab = "Categories" }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Categories",
                        color = if (selectedTab == "Categories") Color.White else Color(0xFF052224),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }



            Spacer(modifier = Modifier.height(16.dp))

            // 🧾 Content for the selected tab
            when (selectedTab) {
                "Spends" -> SpendsContent()
                "Categories" -> CategoriesContent()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
@Composable
fun SpendsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        listOf(
            Triple(Icons.Default.ShoppingCart, "Groceries", "-$100.00"),
            Triple(Icons.Default.AttachMoney, "Others", "$120.00")
        ).forEach { (icon, title, amount) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF3498DB), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontWeight = FontWeight.Bold)
                    Text(text = "17:00 - April 24", color = Color(0xFF3498DB), fontSize = 12.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = if (title == "Groceries") "Pantry" else "Payments", fontSize = 12.sp)
                    Text(
                        text = amount,
                        color = if (title == "Groceries") Color.Blue else Color(0xFF052224),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
@Composable
fun CategoriesContent() {
    val entries = listOf(
        PieChartData.Slice(10f, Color(0xFF0066FF)),   // Groceries
        PieChartData.Slice(11f, Color(0xFF66B2FF)),   // Others
        PieChartData.Slice(79f, Color(0xFF3399FF))    // Remaining/Others
    )
    val data = PieChartData(slices = entries)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PieChart(
            pieChartData = data,
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            LegendItem("Groceries", Color(0xFF0066FF))
            LegendItem("Others", Color(0xFF3399FF))
        }
    }
}


@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CalendarView() {
    var currentMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }

    var showMonthDropdown by remember { mutableStateOf(false) }
    var showYearDropdown by remember { mutableStateOf(false) }

    val daysInMonth = remember(currentMonth, currentYear) {
        YearMonth.of(currentYear, currentMonth).lengthOfMonth()
    }

    val firstDayOfMonth = remember(currentMonth, currentYear) {
        val dayOfWeek = LocalDate.of(currentYear, currentMonth, 1).dayOfWeek.value
        (dayOfWeek + 6) % 7  // Monday = 0
    }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Tháng và năm
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showMonthDropdown = !showMonthDropdown }
            ) {
                Text(
                    text = Month.of(currentMonth).name.lowercase()
                        .replaceFirstChar { it.uppercase() },
                    fontSize = 18.sp,
                    color = Color(0xFF3498DB),
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Month Dropdown",
                    tint = Color(0xFF3498DB)
                )
                DropdownMenu(
                    expanded = showMonthDropdown,
                    onDismissRequest = { showMonthDropdown = false }
                ) {
                    (1..12).forEach { month ->
                        DropdownMenuItem(
                            onClick = {
                                currentMonth = month
                                showMonthDropdown = false
                            },
                            text = {
                                Text(
                                    text = Month.of(month).name.lowercase()
                                        .replaceFirstChar { it.uppercase() })
                            }
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showYearDropdown = !showYearDropdown }
            ) {
                Text(
                    text = currentYear.toString(),
                    fontSize = 18.sp,
                    color = Color(0xFF3498DB),
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Year Dropdown",
                    tint = Color(0xFF3498DB)
                )
                DropdownMenu(
                    expanded = showYearDropdown,
                    onDismissRequest = { showYearDropdown = false }
                ) {
                    val range = (currentYear - 10)..(currentYear + 10)
                    range.forEach { year ->
                        DropdownMenuItem(
                            onClick = {
                                currentYear = year
                                showYearDropdown = false
                            },
                            text = { Text(text = year.toString()) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Header: days of the week
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    color = Color(0xFF3498DB),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    // Days grid
    val totalCells = daysInMonth + firstDayOfMonth
    val rows = (totalCells + 6) / 7
    Column {
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val dayNumber = row * 7 + col - firstDayOfMonth + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNumber in 1..daysInMonth) {
                            Text(text = dayNumber.toString(), color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
data class PieChartData(
    val slices: List<Slice>
) {
    data class Slice(
        val value: Float,
        val color: Color
    )
}
@Composable
fun PieChart(
    pieChartData: PieChartData,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val total = pieChartData.slices.sumOf { it.value.toDouble() }.toFloat()
        var startAngle = -90f

        pieChartData.slices.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }
    }
}






