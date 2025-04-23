package com.example.mainproject.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
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
import com.example.mainproject.NAVIGATION.Routes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mainproject.ui.components.BottomNavigationBar


@Preview (showBackground = true)
@Composable
fun AnalysisScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .background(
                color = Color(0xFFFFFFFF),
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = Color(0xFF3498DB),
                )
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
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                Text(
                    text = "Analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp), // Thêm padding để dễ canh chỉnh
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

                // Separator - thanh trắng ở giữa
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

            // Thanh phần trăm (Progress bar tùy chỉnh)
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
                        .fillMaxWidth(0.3f) // 30%
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

            // Checkbox và dòng thông báo
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

// Nền trắng phía dưới cùng và thanh công cụ bo góc trên
            Box(modifier = Modifier
                .weight(1f)
            ) {
                AnalysisBackgroundBar()


            }
            Box(modifier = Modifier

                .background(Color(0xFFFFFFFF))) {
                BottomNavigationBar(
                    selectedItem = TODO(),
                    onItemClick = TODO(),
                    items = TODO()
                )

            }


        }
    }
}
@Composable
fun AnalysisBackgroundBar() {
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
                val tabs = listOf("Daily", "Weekly", "Monthly", "Year")
                var selectedTab by remember { mutableStateOf("Daily") }

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
                            IconButton(onClick = { }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Calendar", tint = Color(0xFF052224))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.White, shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chart Placeholder", color = Color.Gray)
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