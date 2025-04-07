package com.example.mainproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
//import com.example.mainproject.Navigation.Routes
import com.example.mainproject.R


@Composable
fun MainScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.mainColor_other)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 32.dp)
            )

            // Nút "Sign In"
            Button(
                onClick = {
                    // Điều hướng đến màn hình Sign In
//                    navController.navigate(route = Routes.SIGN_IN)
                },
                modifier = Modifier
                    .width(200.dp) // Đặt chiều rộng cụ thể là 200dp
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.buttonColor),
                    contentColor = colorResource(id = R.color.textColor)
                )
            ) {
                Text(text = "Sign In")
            }

            // Nút "Sign Up"
            Button(
                onClick = {
                    // Điều hướng đến màn hình Sign Up
//                    navController.navigate(Routes.SIGN_UP)
                },
                modifier = Modifier
                    .width(200.dp) // Đặt chiều rộng cụ thể là 200dp
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.buttonColor),
                    contentColor = colorResource(id = R.color.textColor)
                )
            ) {
                Text(text = "Sign Up")
            }

            // Liên kết "Forgot your account?"
            ClickableText(
                text = AnnotatedString("Forgot your account?"),
                onClick = { /* Xử lý sự kiện quên tài khoản */ },
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = colorResource(id = R.color.textColor)
                )
            )
        }
    }
}
