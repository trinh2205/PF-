package com.example.mainproject.ui.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.mainproject.NAVIGATION.AuthViewModelFactory
import com.example.mainproject.NAVIGATION.Routes
//import com.example.mainproject.Navigation.Routes
import com.example.mainproject.R
import com.example.mainproject.ui.auth.AuthViewModel
import com.example.mainproject.viewModel.AppViewModel
import kotlinx.coroutines.launch


@Composable
fun SignIn(navController: NavController,authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(viewModel<AppViewModel>()))) {
    Box(modifier = Modifier.fillMaxSize().background(colorResource( id = R.color.mainColor))) {
        // Layer 1: Background + "Welcome"
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Welcome",
                fontSize = 48.sp,
                color = colorResource(id = R.color.textColor),
                fontWeight = FontWeight.Bold
            )
        }

        // Layer 2: Form đăng nhập với bo góc
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f) // Chiếm 75% màn hình, bạn có thể điều chỉnh
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp), // Bo góc trên
            color = colorResource(id = R.color.mainColor_other) // Màu nền form
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 50.dp, vertical = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Các trường nhập và nút đăng nhập
                LoginForm(navController)
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun LoginForm(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val tag = "SignInScreen"
    val scope = rememberCoroutineScope()
    val state = viewModel.authState.collectAsState() // Lấy AuthState

    OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        label = { Text(text = "Email") },
        placeholder = { Text(text = "example@example.com") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Password
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(text = "Password") },
        placeholder = { Text(text = "Enter your password") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Toggle Password Visibility"
                )
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            Log.d(tag, "Đăng nhập với Email: $username, Mật khẩu: $password")
            scope.launch {
                viewModel.signIn(username, password)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.mainColor),
            contentColor = colorResource(id = R.color.mainColor_other)
        )
    ) {
        Text(text = "Sign in")
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (state.value.signInState.isLoading) { // Truy cập signUpState
        CircularProgressIndicator()
    }

    state.value.signInState.errorMessage?.let { error -> // Truy cập signUpState
        Text(text = error, color = MaterialTheme.colorScheme.error)
    }

    if (state.value.signInState.isSuccess) { // Truy cập signUpState
        Text(text = "Đăng ký thành công!", color = Color.Green)
         navController.navigate(route = com.example.mainproject.NAVIGATION.Routes.HOME)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "forgot password ?",
        fontSize = 14.sp,
        color = colorResource(id = R.color.textColor)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            // navController.navigate(Routes.SIGN_UP)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.buttonColor),
            contentColor = colorResource(id = R.color.textColor)
        )
    ) {
        Text(text = "Sign up")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(text = "or sign up with")

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Handle Facebook login */ },
            modifier = Modifier.size(48.dp) // Kích thước icon
                .clip(CircleShape) // Bo tròn icon
        ) {
            Icon(
                painter = painterResource(id = R.drawable.facebook),
                contentDescription = "Login with Facebook",
                tint = Color.Unspecified
            )
        }

        IconButton(
            onClick = { /* Handle Google login */ },
            modifier = Modifier.size(48.dp).clip(CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Login with Google",
                tint = Color.Unspecified
            )
        }
    }
}