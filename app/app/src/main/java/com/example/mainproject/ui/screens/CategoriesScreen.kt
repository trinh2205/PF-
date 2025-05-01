package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.Data.model.Category
import com.example.mainproject.R
import com.example.mainproject.ui.components.*
import com.example.mainproject.viewModel.TransactionViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun CategoriesScreen(navController: NavController, viewModel: TransactionViewModel = viewModel()) {
    val textField1 = remember { mutableStateOf("") }
    val categoriesMap by viewModel.categories.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val categoriesList = categoriesMap.values.toList()
    var expenseAnalysisChecked by remember { mutableStateOf(true) }

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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
        },
        topBar = {
            CustomHeader(
                title = stringResource(R.string.categories_title),
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
                        text = "-${formatter.format(totalExpense)}",
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
                Column {
                    OutlinedTextField(
                        value = textField1.value,
                        onValueChange = { textField1.value = it },
                        label = { Text("Category Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (textField1.value.isNotBlank()) {
                                val newCategory = Category(
                                    id = viewModel.generateCategoryId(),
                                    name = textField1.value,
                                    type = "Expense",
                                    date = java.time.LocalDateTime.now().toString(),
                                    iconId = textField1.value // Ánh xạ icon dựa trên title
                                )
                                viewModel.addCategory(newCategory)
                                textField1.value = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Add Category")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(all = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categoriesList) { category ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                GridItem(
                                    id = category.id,
                                    text = category.name,
                                    sizeItem = 100.dp,
                                    colorText = Color.Black,
                                    colorBackground = Color.White,
                                    activeTextColor = Color.White,
                                    activeBackgroundColor = colorResource(id = R.color.mainColor),
                                    roundedCorner = 16,
                                    iconType = category.iconId,
                                    categoryIcons = categoryIcons,
                                    onClick = { categoryId ->
                                        navController.navigate("itemScreen/${category.id}/${category.name}")
                                    }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = category.name,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}