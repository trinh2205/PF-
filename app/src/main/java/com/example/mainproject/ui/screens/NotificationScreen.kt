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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mainproject.R
import com.example.mainproject.data.model.Notification
import com.example.mainproject.data.model.TransactionBE
import com.example.mainproject.data.repository.NotificationRepository
import com.example.mainproject.viewModel.AppViewModel
import com.example.mainproject.viewModel.NotificationViewModel
import com.example.mainproject.viewModel.TransactionViewModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun NotificationScreen(navController: NavController, appViewModel: AppViewModel) {
    val userInfo by appViewModel.currentUser.collectAsState()
    val notificationRepository = remember { NotificationRepository.create() }
    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModel.provideFactory(notificationRepository, userInfo?.userId)
    )
    val notifications by notificationViewModel.notifications.collectAsState()
    val isLoadingNotifications by notificationViewModel.isLoading.collectAsState()
    val mainColor = colorResource(id = R.color.mainColor)

    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModel.provideFactory(notificationRepository, userInfo?.userId)
    )
    val transactionsMap by transactionViewModel.transactionsBE.collectAsState()
    val isLoadingTransactions by transactionViewModel.isLoadingTransactions.collectAsState()

    val groupedTransactions = remember(transactionsMap) {
        transactionsMap.values.groupBy { transaction ->
            val transactionDate = LocalDate.parse(transaction.date)
            when {
                transactionDate.isEqual(LocalDate.now()) -> "Hôm nay"
                transactionDate.isEqual(LocalDate.now().minusDays(1)) -> "Hôm qua"
                transactionDate.isAfter(LocalDate.now().minusWeeks(1).minusDays(1)) -> "Tuần này"
                transactionDate.isAfter(LocalDate.now().minusMonths(1).minusDays(1)) -> "Tháng này"
                transactionDate.isAfter(LocalDate.now().minusYears(1).minusDays(1)) -> "Năm này"
                else -> "Cũ hơn"
            }
        }.toSortedMap(compareByDescending<String> {
            when (it) {
                "Hôm nay" -> 0
                "Hôm qua" -> 1
                "Tuần này" -> 2
                "Tháng này" -> 3
                "Năm này" -> 4
                else -> 5
            }
        })
    }

    Scaffold(
        modifier = Modifier.background(mainColor),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3498DB))
                        .padding(horizontal = 12.dp, vertical = 25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Giao Dịch", // Giữ nguyên tiêu đề "Giao Dịch"
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(28.dp))
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(mainColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color(0xFFF4FFF9))
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                if (isLoadingTransactions) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = mainColor)
                    }
                } else if (transactionsMap.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chưa có giao dịch nào.",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        groupedTransactions.forEach { (timeTag, transactionsForTag) ->
                            item {
                                Text(
                                    text = timeTag,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(transactionsForTag) { transaction ->
                                TransactionBEItemUI(transaction = transaction)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionBEItemUI(transaction: TransactionBE) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hình vuông bo tròn chứa icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (transaction.type == "expense") Color(0xFFF44336).copy(alpha = 0.8f) else Color(0xFF4CAF50).copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (transaction.type == "expense") Icons.Filled.ShoppingCart else Icons.Filled.AttachMoney,
                contentDescription = "Icon giao dịch",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        // Phần giữa: Title và Description
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = transaction.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.message,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            // Thông tin type và số tiền
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.type.replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${if (transaction.type == "income") "+" else "-"}${String.format("%,.0f", transaction.amount)}đ",
                    fontSize = 12.sp,
                    color = if (transaction.type == "income") Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))

        // Phần bên phải: Thông tin thời gian chi tiết (chỉ ngày tháng năm)
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = LocalDate.parse(transaction.date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                fontSize = 12.sp,
                color = Color.DarkGray
            )
            // Bạn có thể thêm giờ nếu cần, nhưng model chỉ có ngày
        }
    }
    Divider()
}

//package com.example.mainproject.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.AttachMoney
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.ShoppingCart
//import androidx.compose.material3.Divider
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.mainproject.R
//import com.example.mainproject.data.model.TransactionBE
//import com.example.mainproject.viewModel.AppViewModel
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//import java.time.temporal.ChronoUnit
//import java.util.UUID
//
//@Composable
//fun NotificationScreen(navController: NavController, appViewModel: AppViewModel) {
//    val sampleTransactions = remember {
//        listOf(
//            TransactionBE(UUID.randomUUID().toString(), "1", "Mua sắm online", 55000.0, "expense", LocalDate.now().toString(), "Quần áo"),
//            TransactionBE(UUID.randomUUID().toString(), "2", "Lương tháng", 2000000.0, "income", LocalDate.now().toString(), "Lương tháng 5"),
//            TransactionBE(UUID.randomUUID().toString(), "3", "Ăn tối", 30000.0, "expense", LocalDate.now().minusDays(1).toString(), "Nhà hàng X"),
//            TransactionBE(UUID.randomUUID().toString(), "4", "Đầu tư", 100000.0, "expense", LocalDate.now().minusDays(3).toString(), "Mua cổ phiếu"),
//            TransactionBE(UUID.randomUUID().toString(), "5", "Tiền thưởng", 500000.0, "income", LocalDate.now().minusWeeks(2).toString(), "Thưởng dự án"),
//            TransactionBE(UUID.randomUUID().toString(), "6", "Tiền điện", 150000.0, "expense", LocalDate.now().minusMonths(1).toString(), "Tháng 4"),
//            TransactionBE(UUID.randomUUID().toString(), "7", "Thu nhập khác", 200000.0, "income", LocalDate.now().minusYears(1).toString(), "Bán đồ cũ"),
//            TransactionBE(UUID.randomUUID().toString(), "8", "Chi phí khác", 70000.0, "expense", LocalDate.now().minusYears(1).minusMonths(3).toString(), "Sửa xe"),
//        ).sortedByDescending { LocalDate.parse(it.date) }
//    }
//
//    val groupedTransactions = sampleTransactions.groupBy { transaction ->
//        val transactionDate = LocalDate.parse(transaction.date)
//        when {
//            transactionDate.isEqual(LocalDate.now()) -> "Hôm nay"
//            transactionDate.isEqual(LocalDate.now().minusDays(1)) -> "Hôm qua"
//            transactionDate.isAfter(LocalDate.now().minusWeeks(1).minusDays(1)) -> "Tuần này"
//            transactionDate.isAfter(LocalDate.now().minusMonths(1).minusDays(1)) -> "Tháng này"
//            transactionDate.isAfter(LocalDate.now().minusYears(1).minusDays(1)) -> "Năm này"
//            else -> "Cũ hơn"
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(colorResource(id = R.color.mainColor))
//                    .padding(horizontal = 16.dp, vertical = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Quay lại",
//                        tint = Color.White,
//                        modifier = Modifier.size(28.dp)
//                    )
//                }
//                Box(
//                    modifier = Modifier.weight(1f),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "Giao Dịch",
//                        fontSize = 20.sp,
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//                Spacer(modifier = Modifier.width(28.dp)) // Để cân bằng icon bên trái
//            }
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp)
//        ) {
//            groupedTransactions.forEach { (timeTag, transactionsForTag) ->
//                item {
//                    Text(
//                        text = timeTag,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 16.sp,
//                        color = Color.Black,
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    )
//                    Divider()
//                }
//                items(transactionsForTag) { transaction ->
//                    TransactionBEItemUI(transaction = transaction)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun TransactionBEItemUI(transaction: TransactionBE) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        // Hình vuông bo tròn chứa icon
//        Box(
//            modifier = Modifier
//                .size(40.dp)
//                .clip(RoundedCornerShape(8.dp))
//                .background(if (transaction.type == "expense") Color(0xFFF44336).copy(alpha = 0.8f) else Color(0xFF4CAF50).copy(alpha = 0.8f)),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = if (transaction.type == "expense") Icons.Filled.ShoppingCart else Icons.Filled.AttachMoney,
//                contentDescription = "Icon giao dịch",
//                tint = Color.White,
//                modifier = Modifier.size(24.dp)
//            )
//        }
//        Spacer(modifier = Modifier.width(16.dp))
//
//        // Phần giữa: Title và Description
//        Column(
//            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = transaction.title,
//                fontWeight = FontWeight.SemiBold,
//                fontSize = 16.sp,
//                color = Color.Black,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//            Text(
//                text = transaction.message,
//                fontSize = 14.sp,
//                color = Color.Gray,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//            // Thông tin type và số tiền
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = transaction.type.replaceFirstChar { it.uppercase() },
//                    fontSize = 12.sp,
//                    color = Color.DarkGray
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "${if (transaction.type == "income") "+" else "-"}${String.format("%,.0f", transaction.amount)}đ",
//                    fontSize = 12.sp,
//                    color = if (transaction.type == "income") Color(0xFF4CAF50) else Color(0xFFF44336),
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//        Spacer(modifier = Modifier.width(16.dp))
//
//        // Phần bên phải: Thông tin thời gian chi tiết (chỉ ngày tháng năm)
//        Column(
//            horizontalAlignment = Alignment.End
//        ) {
//            Text(
//                text = LocalDate.parse(transaction.date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
//                fontSize = 12.sp,
//                color = Color.DarkGray
//            )
//            // Bạn có thể thêm giờ nếu cần, nhưng model chỉ có ngày
//        }
//    }
//}