
package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainproject.ui.components.BottomNavigationBar


@Preview(  showBackground = true  )
@Composable
fun SearchBar() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header Section (nằm trên cùng)
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
                    modifier = Modifier.clickable { /* Handle back */ }
                )
                Text(
                    text = "Search",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }

        // Nội dung bên dưới (search input + form)
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFF4FFF9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                // Search Box ngay dưới header
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE6FFE6), RoundedCornerShape(20.dp)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.Transparent ,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        textColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Các phần còn lại (Category, Date, Report, Button...)
                SearchBackgroundBar()
            }
        }

        BottomNavigationBar(
            selectedItem = TODO(),
            onItemClick = TODO(),
            items = TODO()
        )
    }
}

@Composable
fun SearchBackgroundBar() {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("30 /APR/2023") }
    var selectedReport by remember { mutableStateOf("Income") }


    Spacer(modifier = Modifier.height(24.dp))

    // Categories Dropdown
    Text(text = "Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = selectedCategory,
        onValueChange = { selectedCategory = it },
        placeholder = { Text("Select the category") },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE6FFE6), RoundedCornerShape(20.dp)),
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            textColor = Color.Black,
            placeholderColor = Color.Gray
        ),
        maxLines = 1,
        singleLine = true
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Date Picker
    Text(text = "Date", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = selectedDate,
        onValueChange = { selectedDate = it },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE6FFE6), RoundedCornerShape(20.dp)),
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = null)
        },
        placeholder = { Text("Select date") },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            textColor = Color.Black,
            placeholderColor = Color.Gray
        ),
        maxLines = 1,
        singleLine = true
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Report Type
    Text(text = "Report", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { selectedReport = "Income" }
        ) {
            RadioButton(
                selected = selectedReport == "Income",
                onClick = { selectedReport = "Income" }
            )
            Text(text = "Income")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { selectedReport = "Expense" }
        ) {
            RadioButton(
                selected = selectedReport == "Expense",
                onClick = { selectedReport = "Expense" }
            )
            Text(text = "Expense")
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Search Button
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { /* xử lý tìm kiếm */ },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3498DB),
                contentColor = Color.White
            )
        ) {
            Text("Search")
        }
    }
}



