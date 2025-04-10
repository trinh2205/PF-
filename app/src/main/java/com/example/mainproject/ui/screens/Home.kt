package com.example.mainproject.ui.screens
import androidx.activity.compose.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.runtime.getValue
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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Preview(showBackground = true)
@Composable
fun Home() {
    val textField1 = remember { mutableStateOf("") }
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
            }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 25.dp,start = 38.dp,end = 38.dp,)
            ) {
                Column(
                    modifier = Modifier
                        .padding(end = 12.dp,)
                        .weight(1f)
                ){
                    Text("Hi, Welcome Back",
                        color = Color(0xFF052224),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Good Morning",
                        color = Color(0xFF052224),
                        fontSize = 14.sp,
                    )

                }
                Image(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification",
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(40.dp))
                        .width(30.dp)
                        .height(30.dp)
                        .background(Color(0xFFDFF7E2))
                )
            }
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
                BottomBackgroundBar()


            }
            Box(modifier = Modifier

                .background(Color(0xFFFFFFFF))) {
                BottomNavigationBar()

            }
        }
    }
}

@Composable
fun BottomBackgroundBar(
    selectedTimeFilter: String = "Weekly",
    onTimeFilterChange: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(Color(0xFFF4FFF9)) // màu nền trắng ngà
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // 1. Thẻ tổng quan tài chính
            FinancialCard()

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Bộ lọc thời gian
            TimeFilterToggle(
                selected = selectedTimeFilter,
                onSelected = onTimeFilterChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Danh sách giao dịch
            TransactionList(filter = selectedTimeFilter)
        }
    }
}


@Composable
fun BottomNavigationBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(Color(0xFFDFF7E2)) // Màu nền xanh nhạt
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icons = listOf(
                Icons.Default.Home,
                Icons.Default.BarChart,
                Icons.Default.SwapHoriz,
                Icons.Default.Layers,
                Icons.Default.Person
            )

            icons.forEachIndexed { index, icon ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == 0) Color(0xFF3498DB) else Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (index == 0) Color.White else Color(0xFF052224),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun TimeFilterToggle(selected: String, onSelected: (String) -> Unit) {
    val options = listOf("Daily", "Weekly", "Montly")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // 👈 giống với FinancialCard
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
            .background(Color(0xFF3498DB)) // Màu xanh dương
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Cột trái: Icon và mô tả
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
                        // Vòng tròn nền (progress track) màu trắng
                        drawArc(
                            color = Color.White,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Vòng màu xanh dương đậm - phần progress
                        drawArc(
                            color = Color(0xFF007BFF), // Xanh dương đậm giống ảnh
                            startAngle = -90f,
                            sweepAngle = 180f, // Tùy chỉnh % progress (270 = 75%)
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Icon ở giữa vòng tròn
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

            // Thanh chia trắng
            Divider(
                color = Color.White,
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp)
            )

            // Cột phải: Thông tin thu chi
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
                            "$4.000.00",
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
                            "-$100.00",
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

data class Transaction(
    val title: String,
    val time: String,
    val period: String,
    val amount: String,
    val isPositive: Boolean,
    val icon: ImageVector
)

@Composable
fun TransactionItem(transaction: Transaction) {
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
                    imageVector = transaction.icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(transaction.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(transaction.time, fontSize = 12.sp, color = Color(0xFF1D71B8))
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(transaction.period, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = transaction.amount,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isPositive) Color.Black else Color.Red
            )
        }
    }
}
@Composable
fun TransactionList(filter: String) {
    val allTransactions = listOf(
        Transaction("Salary", "18:27 - April 30", "Monthly", "$4.000,00", true, Icons.Default.AttachMoney),
        Transaction("Groceries", "17:00 - April 24", "Daily", "-$100,00", false, Icons.Default.LocalGroceryStore),
        Transaction("Groceries", "17:00 - April 24", "Weekly", "-$100,00", false, Icons.Default.LocalGroceryStore),
        Transaction("Rent", "8:30 - April 15", "Weekly", "-$674,40", false, Icons.Default.Home)
    )

    val filtered = allTransactions.filter { it.period == filter }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        filtered.forEach { transaction ->
            TransactionItem(transaction)
        }
    }
}


