package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mainproject.NAVIGATION.Routes
import com.example.mainproject.ui.components.BottomNavigationBar

//@Preview(showBackground = true)
@Composable
fun QuickAnalysis(navController: NavController) {

//    val textField2 = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .background(
                color = Color(0xFFFFFFFF),
            )
    )
    {
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                Text(
                    text = "Quickly Analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
            FinancialCard(
//                onClick = {
//                    navController.navigate(Routes.ANALYTICS)
//                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier
                .weight(1f)
            ) {
                QuickAnalysisBackgroundBar()


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
fun QuickAnalysisBackgroundBar() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(Color(0xFFF4FFF9)) // màu nền trắng ngà
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp)) // bo góc
                    .background(Color(0xFFFFFFFF))
                    .padding(15.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .offset(x = (-8).dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top

                    ) {
                        Text(
                            text = "April Expenses",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF052224)
                        )

                        Row {
                            IconButton(
                                onClick = {  },
                                modifier = Modifier
                                    .size(25.dp)
                                    .background(Color(0xFF3498DB), shape = CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(30.dp))
                            IconButton(
                                onClick = {  },
                                modifier = Modifier
                                    .size(25.dp)
                                    .background(Color(0xFF3498DB), shape = CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Calendar",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Placeholder for chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(Color(0xFFEFF7F9), shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chart placeholder here", color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // 3. Danh sách giao dịch
//            TransactionList(filter = "Monthly")
//            TransactionList(filter = "Weekly")
        }
    }
}