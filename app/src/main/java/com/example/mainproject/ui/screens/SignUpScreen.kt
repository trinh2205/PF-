package com.example.mainproject.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mainproject.R
import com.example.mainproject.ui.auth.AuthViewModel
import kotlinx.coroutines.launch


@Composable
fun SignUp(
    navController: NavController,
    viewModel: AuthViewModel = viewModel(),
    authViewModel: AuthViewModel
) {
    Box(modifier = Modifier.fillMaxSize().background(colorResource( id = R.color.mainColor))) {
        // Layer 1: Background + "Welcome"
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                color = colorResource(id = R.color.textColor)
            )
        }

        // Layer 2: Form đăng nhập với bo góc
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f) // Chiếm 75% màn hình, bạn có thể điều chỉnh
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp), // Bo góc trên
            color = colorResource(id = R.color.mainColor_other) // Màu nền form
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Các trường nhập và nút đăng nhập
                SignUpForm(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SignUpForm(viewModel: AuthViewModel = viewModel()) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val tag = "SignUpScreen"
    val scope = rememberCoroutineScope()
    val state = viewModel.authState.collectAsState() // Lấy AuthState

    Column(modifier = Modifier.fillMaxWidth()) {
        // Full Name
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(text = "Full Name") },
            placeholder = { Text(text = "Nguyen Van A") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            placeholder = { Text(text = "example@example.com") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(text = "Phone Number") },
            placeholder = { Text(text = "0123 456 789") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Date of Birth
        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text(text = "Date of Birth") },
            placeholder = { Text(text = "DD/MM/YYYY") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                imeAction = ImeAction.Next
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

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(text = "Confirm Password") },
            placeholder = { Text(text = "Re-enter your password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle Confirm Password Visibility"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "By continuing, you agree to our \nTerms of Use and Privacy Policy.",
            fontSize = 14.sp,
            color = colorResource(id = R.color.textColor)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Button
        Button(
            onClick = {
                Log.d(
                    tag,
                    "Đăng ký với: Full Name=$fullName, Email=$email, Phone=$phone, DOB=$dob, Password=$password, Confirm Password=$confirmPassword"
                )
                scope.launch {
                    viewModel.signUp(email, password, fullName)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.buttonColor),
                contentColor = colorResource(id = R.color.textColor)
            )
        ) {
            Text(text = "Sign Up")
        }

        if (state.value.signUpState.isLoading) { // Truy cập signUpState
            CircularProgressIndicator()
        }

        state.value.signUpState.errorMessage?.let { error -> // Truy cập signUpState
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        if (state.value.signUpState.isSuccess) { // Truy cập signUpState
            Text(text = "Đăng ký thành công!", color = Color.Green)
            // navController.navigate(Routes.HOME)
        }
    }
}
