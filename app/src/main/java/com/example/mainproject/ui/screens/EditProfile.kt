package com.example.mainproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.mainproject.R
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.NavigationItem


@Preview(showBackground = true)
@Composable
fun Editprofile() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // 👈 Bo góc toàn màn
            .background(Color.White)
    ) {
        // Nền xanh phía trên

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // ✅ Chỉ chiếm 1 phần màn hình
                .background(Color(0xFF3498DB))
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                Text(
                    text = "Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
        }

        // Nền trắng bo góc
        EditProfileBackgroundBar(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp) // ✅ Đẩy xuống dưới phần xanh
        )

        // Avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp), // đặt avatar giữa
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.LightGray, CircleShape)
                    .shadow(4.dp, CircleShape)
            )
        }

        // Bottom bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFFEFFFF6))
        ) {
//            BottomNavigationBar(
//                selectedItem = currentRoute ?: NavigationItem.DefaultItems.first().route,
//                onItemClick = { item ->
//                    navController.navigate(item.route) {
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                }
//            )
        }
    }
}


@Composable
fun EditProfileBackgroundBar(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("John Smith") }
    var phone by remember { mutableStateOf("+44 555 5555 55") }
    var email by remember { mutableStateOf("example@example.com") }
    var pushNotifications by remember { mutableStateOf(true) }
    var darkTheme by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(Color(0xFFF4FFF9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tên và ID
            Text("Châu Trinh", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("ID: 22052005", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))

            // ✅ Tiêu đề Account Settings
            Text(
                text = "Account Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0A2F35),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Username
            Text(
                text = "Username",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFDFF7E7),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Color.Black
                ),
                textStyle = TextStyle(fontSize = 10.sp), // 👈 chữ nhỏ hơn
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp), // 👈 giảm chiều cao
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            Text(
                text = "Phone",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFDFF7E7),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Color.Black
                ),
                textStyle = TextStyle(fontSize = 10.sp), // 👈 chữ nhỏ hơn
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp), // 👈 giảm chiều cao
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email
            Text(
                text = "Email Address",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFDFF7E7),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Color.Black
                ),
                textStyle = TextStyle(fontSize = 10.sp), // 👈 chữ nhỏ hơn
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp), // 👈 giảm chiều cao
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Push Notifications switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Push Notifications", fontSize = 16.sp)
                Switch(
                    checked = pushNotifications,
                    onCheckedChange = { pushNotifications = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3498DB))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dark Theme switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Turn Dark Theme", fontSize = 16.sp)
                Switch(
                    checked = darkTheme,
                    onCheckedChange = { darkTheme = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3498DB))
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Update Profile button
            Button(
                onClick = { /* Handle update */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3498DB)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Update Profile", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}