package com.example.mainproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mainproject.data.repository.UserRepository
import com.example.mainproject.viewModel.BankViewModel
import com.example.mainproject.viewModel.BankViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SaveBankScreen(navController: NavController, bankViewModel: BankViewModel) {
    SaveBankScreenContent(navController = navController, bankViewModel = bankViewModel)
}

@Composable
fun SaveBankScreenContent(navController: NavController, bankViewModel: BankViewModel) {
    val context = LocalContext.current

    var userName by remember { mutableStateOf("") }
    var bankCode by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }

    val accountBankInfoState by bankViewModel.accountBankInfo.collectAsState(initial = null)

    val buttonBackgroundColor = Color(0xFF3498DB) // Màu xanh bạn muốn

    LaunchedEffect(accountBankInfoState) {
        accountBankInfoState?.let {
            userName = it.userName
            bankCode = it.bankCode
            cardHolderName = it.cardHolderName
            bankName = it.bankName
            balance = it.balance.toString() // Hiển thị số dư đã lưu
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF3498DB))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3498DB))
                        .padding(horizontal = 12.dp, vertical = 25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Account Bank",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(28.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Tên Người Dùng") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = bankCode,
                onValueChange = { bankCode = it },
                label = { Text("Mã Số Ngân Hàng") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cardHolderName,
                onValueChange = { cardHolderName = it },
                label = { Text("Tên Trên Thẻ") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Tên Ngân Hàng") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = balance,
                onValueChange = {
                    balance = it.filter { char -> char.isDigit() }
                },
                label = { Text("Số Tiền Hiện Có") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        bankViewModel.saveAccountBankInfo(
                            bankCode = bankCode,
                            userName = userName,
                            cardHolderName = cardHolderName,
                            bankName = bankName,
                            balance = balance.toDoubleOrNull() ?: 0.0
                        ) { success, message ->
                            if (success) {
                                Toast.makeText(context, "Thông tin ngân hàng đã được lưu!", Toast.LENGTH_SHORT).show()
                                bankViewModel.refreshAccountBankInfo()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Lỗi lưu thông tin: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = buttonBackgroundColor, // Đặt màu nền ở đây
                        contentColor = Color.White // Màu chữ (tùy chọn)
                        // Bạn có thể tùy chỉnh các màu khác như disabledContainerColor, disabledContentColor, v.v.
                    )
                ) {
                    Text("Lưu")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        userName = ""
                        bankCode = ""
                        cardHolderName = ""
                        bankName = ""
                        balance = ""
                    },
                    modifier = Modifier.weight(1f).background(Color(0xFF3498DB))
                ) {
                    Text("Reset")
                }
            }
        }
    }
}