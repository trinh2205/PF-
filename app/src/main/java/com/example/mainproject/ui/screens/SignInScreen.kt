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
//import androidx.media3.common.util.Log
//import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.mainproject.NAVIGATION.AuthViewModelFactory
import com.example.mainproject.NAVIGATION.Routes
//import com.example.mainproject.Navigation.Routes
import com.example.mainproject.R
import com.example.mainproject.ui.auth.AuthViewModel
import com.example.mainproject.viewModel.AppViewModel
import dagger.hilt.android.UnstableApi
import kotlinx.coroutines.launch


@Composable
fun SignIn(
    navController: NavController,
    viewModel: AuthViewModel,
    emailFromSignUp: String? = null,
    passwordFromSignUp: String? = null
) {
    var email by remember { mutableStateOf(emailFromSignUp ?: "") }
    var password by remember { mutableStateOf(passwordFromSignUp ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val state by viewModel.authState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(colorResource(id = R.color.mainColor))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign In",
                fontSize = 32.sp,
                color = colorResource(id = R.color.textColor)
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = colorResource(id = R.color.mainColor_other)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        scope.launch {
                            viewModel.signIn(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.buttonColor),
                        contentColor = colorResource(id = R.color.textColor)
                    )
                ) {
                    Text(text = "Sign In")
                }

                if (state.signInState.isLoading) {
                    CircularProgressIndicator()
                }

                state.signInState.errorMessage?.let { error ->
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }

                if (state.signInState.isSuccess) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                    Text(text = "Đăng nhập thành công!", color = Color.Green)
                }
            }
        }
    }
}
