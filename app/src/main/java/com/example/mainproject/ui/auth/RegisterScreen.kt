package com.example.mainproject.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mainproject.ui.components.AuthButton
import com.example.mainproject.ui.components.InputField

@Composable
fun RegisterScreen(authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Đăng ký tài khoản", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        InputField(value = email, onValueChange = { email = it }, label = "Email")
        InputField(value = phone, onValueChange = { phone = it }, label = "Số điện thoại", keyboardType = KeyboardType.Phone)
        InputField(value = password, onValueChange = { password = it }, label = "Mật khẩu", isPassword = true)
        InputField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Nhập lại mật khẩu", isPassword = true)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (password == confirmPassword) {
                authViewModel.register(email, password, phone, context)
            } else {
                Toast.makeText(context, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Đăng ký")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton("Đăng ký với Google", onClick = { authViewModel.signInWithGoogle(context) })
        AuthButton("Đăng ký với Facebook", onClick = { authViewModel.signInWithFacebook(context) })
    }
}
