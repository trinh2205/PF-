package com.example.mainproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mainproject.NAVIGATION.Routes
import com.example.mainproject.R
import com.example.mainproject.ui.components.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

//@Preview(showBackground = true) pass navController de handle logout va cac backend khac
@Composable
fun Profile(navController: NavController) {
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
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
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
        ProfileBackgroundBar(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp) // ✅ Đẩy xuống dưới phần xanh
            ,
            navController = navController
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
            BottomNavigationBar(
                selectedItem = TODO(),
                onItemClick = TODO(),
                items = TODO()
            )
        }
    }
}


@Composable
fun ProfileBackgroundBar(modifier: Modifier = Modifier, navController: NavController) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)) //BO GÓC TRÊN
            .background(Color(0xFFF4FFF9)) // màu nền trắng ngà
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 70.dp), // chừa chỗ phía trên cho avatar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tên và ID
            Text("Châu Trinh", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("ID: 22052005", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))

            // Danh sách chức năng
            ProfileOption(icon = Icons.Default.Person, title = "Edit Profile", onClick = {})
            ProfileOption(icon = Icons.Default.Settings, title = "Setting", onClick = {})
            ProfileOption(icon = Icons.Default.Logout, title = "Logout", onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Routes.MAIN_SCREEN)
            })
        }
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF3498DB),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp)
    }
}