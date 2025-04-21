package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mainproject.Data.model.Category
import com.example.mainproject.Data.model.ListCategories
import com.example.mainproject.R
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.CustomHeader
import com.example.mainproject.viewModel.TransactionViewModel
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ItemScreen(
    navController: NavController,
    listItem: ListCategories,
    viewModel: TransactionViewModel = viewModel(),
    previewTransactions: Map<String, List<Category>>? = null // Đảm bảo import đúng model Category
){    val textField1 = remember { mutableStateOf("") }
    var selectedBottomNav by remember { mutableStateOf("home") }


    // Lấy danh sách giao dịch đã lọc từ ViewModel dựa trên listItem.name
    val transactionsByCategory by viewModel.getTransactionsByCategoryName(listItem.name)
        .collectAsState(initial = emptyList())

    // Nhóm các giao dịch đã lọc theo tháng
    val groupedTransactionsByMonth = remember(transactionsByCategory) {
        transactionsByCategory.groupBy {
            it.date?.substringAfterLast("/")?.substringBefore("/") ?: ""
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedBottomNav,
                onItemClick = { newItem -> selectedBottomNav = newItem.route }
            )
        },
        topBar = {
            CustomHeader(
                title = listItem.name,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF3498DB))
        ) {
            // Loại bỏ Column với verticalScroll
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {}

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
                        text = "$7,783.00", // Replace with dynamic data later
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
                        text = "-$1,187.40", // Replace with dynamic data later
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
                        "30%", // Replace with dynamic data
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Text(
                    "$20,000.00", // Replace with dynamic data
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
                    checked = true, // Replace with dynamic state
                    onCheckedChange = {}
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
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color(0xFFF4FFF9))
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 40.dp)) {
                    groupedTransactionsByMonth.forEach { (month, transactions) ->
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
                                        androidx.compose.material.Icon(
                                            imageVector = listItem.icon,
                                            contentDescription = listItem.name,
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(transaction.title ?: "", fontWeight = FontWeight.Bold)
                                        Text(
                                            transaction.date ?: "",
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
                                        text = listItem.name, // Hiển thị tên danh mục cha
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
                                        text = "${if (transaction.amount ?: 0.0 < 0) "-" else ""}$${
                                            String.format(
                                                "%.2f",
                                                Math.abs(transaction.amount ?: 0.0)
                                            )
                                        }",
                                        fontWeight = FontWeight.Bold,
                                        color = if ((transaction.amount ?: 0.0) > 0) Color(0xFF4CAF50) else Color(
                                            0xFFF44336
                                        ),
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

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun ItemScreenPreview() {
//    val navController = rememberNavController()
//    val listItem = ListCategories(1, "Ăn uống", Icons.Filled.Fastfood)
//    val viewMD = viewModel<TransactionViewModel>()
//    val dummyTransactions = listOf(
//        Category(id = 101, title = "Bữa trưa", amount = -55.75, date = "19/04/2025"),
//        Category(id = 102, title = "Mua sắm", amount = -120.50, date = "15/04/2025"),
//        Category(id = 103, title = "Lương", amount = 2500.00, date = "30/04/2025"),
//        // ... thêm các dummy transaction khác
//    )
//    val groupedDummyTransactions = dummyTransactions.groupBy {
//        it.date?.substringAfterLast("/")?.substringBefore("/") ?: ""
//    }
//
//    ItemScreen(
//        navController = navController,
//        listItem = listItem,
//        viewModel = viewMD,
//        previewTransactions = groupedDummyTransactions // Truyền tham số này
//    )
//}