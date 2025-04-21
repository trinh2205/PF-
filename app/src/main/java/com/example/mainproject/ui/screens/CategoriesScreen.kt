package com.example.mainproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
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
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mainproject.R
import com.example.mainproject.Data.model.Category
import com.example.mainproject.Data.model.ListCategories
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.CustomHeader
import com.example.mainproject.ui.components.GridItem
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.ui.components.NavigationItem
import com.example.mainproject.viewModel.TransactionViewModel

@Composable
fun CategoriesScreen(navController: NavController, viewModel: TransactionViewModel = viewModel()) {
    val textField1 = remember { mutableStateOf("") }
    var selectedBottomNav by remember { mutableStateOf("home") }
    val addNewCategory = Category(
        id = -1, // Hoặc một ID không trùng lặp khác
        title = "", // Sử dụng string resource cho đa ngôn ngữ
        type = emptyList(), // Hoặc một giá trị đặc biệt khác
        detail = "",
        amount = 0.0,
        date = ""
    )

    val categoriesMap by viewModel.categories.collectAsState()
    val categoriesList = categoriesMap.values.toList() // Chuyển Map thành List để dùng với items

//    val categories = remember {
//        mutableStateOf(
//            listOf(
//                Category(id = 1, title = "Food", type = listOf(ListCategories(id = 1, name = "Ăn uống", icon = Icons.Filled.Fastfood)), detail = "", amount = 0.0, date = ""),
//                Category(id = 2, title = "Electronics", type = listOf(ListCategories(id = 2, name = "Công nghệ", icon = Icons.Filled.Android)), detail = "", amount = 0.0, date = ""),
//                Category(id = 3, title = "Book", type = listOf(ListCategories(id = 3, name = "Văn học", icon = Icons.Filled.Book)), detail = "", amount = 0.0, date = ""),
//                Category(id = 4, title = "Clothes", type = listOf(ListCategories(id = 4, name = "Thời trang", icon = Icons.Filled.Checkroom)), detail = "", amount = 0.0, date = ""),
//                Category(id = 5, title = "House Hold", type = listOf(ListCategories(id = 5, name = "Đời sống", icon = Icons.Filled.Home)), detail = "", amount = 0.0, date = ""),
//                Category(id = 6, title = "Sports", type = listOf(ListCategories(id = 6, name = "Giải trí", icon = Icons.Filled.PlayArrow)), detail = "", amount = 0.0, date = ""),
//                Category(id = 7, title = "Travel", type = listOf(ListCategories(id = 7, name = "Khám phá", icon = Icons.Filled.Explore)), detail = "", amount = 0.0, date = ""),
//                Category(id = 8, title = "Education", type = listOf(ListCategories(id = 8, name = "Học tập", icon = Icons.Filled.School)), detail = "", amount = 0.0, date = ""),
//                Category(id = 9, title = "Health", type = listOf(ListCategories(id = 9, name = "Chăm sóc bản thân", icon = Icons.Filled.Spa)), detail = "", amount = 0.0, date = ""),
//                addNewCategory
//                // Thêm các category khác của bạn vào đây
//            )
//        )
//    }

    val categoryIcons = remember {
        mapOf(
            "Ăn uống" to Icons.Filled.Restaurant,
            "Công nghệ" to Icons.Filled.Android,
            "Văn học" to Icons.Filled.Book,
            "Thời trang" to Icons.Filled.Checkroom,
            "Đời sống" to Icons.Filled.Home,
            "Giải trí" to Icons.Filled.PlayArrow,
            "Khám phá" to Icons.Filled.Explore,
            "Học tập" to Icons.Filled.School,
            "Chăm sóc bản thân" to Icons.Filled.Spa,
            // Thêm các ánh xạ khác của bạn
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = currentRoute ?: NavigationItem.DefaultItems.first().route,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        // ... cấu hình điều hướng (tùy chọn) ...
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
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color(0xFFF4FFF9))
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(all = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoriesList) { listCategory -> // Sử dụng categoriesList
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            GridItem(
                                id = listCategory.id, // Sử dụng ID từ ListCategories
                                text = listCategory.name,
                                sizeItem = 100.dp,
                                colorText = Color.Black,
                                colorBackground = Color.White,
                                activeTextColor = Color.White,
                                activeBackgroundColor = colorResource(id = R.color.mainColor),
                                roundedCorner = 16,
                                iconType = listCategory.name, // Sử dụng tên để ánh xạ icon
                                categoryIcons = categoryIcons,
                                onClick = { categoryId ->
                                    navController.navigate("itemScreen/${listCategory.id}/${listCategory.name}")
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = listCategory.name,
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

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun CategoryPreview() {
//    val navController = rememberNavController() // Tạo một NavController đơn giản cho preview
//    CategoriesScreen(navController = navController)
//}