package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mainproject.R
import com.example.mainproject.ui.components.BottomNavigationBar
import com.example.mainproject.ui.components.CustomHeader
import com.example.mainproject.ui.components.NavigationItem
import com.example.mainproject.viewModel.BankViewModel
import java.text.NumberFormat
import java.util.Locale
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun BankScreen(navController: NavController, bankViewModel: BankViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val mainColor = colorResource(id = R.color.mainColor)
    val context = LocalContext.current

    var userName by remember { mutableStateOf("") }
    var bankCode by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var transferAmountStr by remember { mutableStateOf("") }

    val accountBankInfoState by bankViewModel.accountBankInfo.collectAsState(initial = null)
    val accountState by bankViewModel.account.collectAsState(initial = null)

    LaunchedEffect(accountBankInfoState, accountState) {
        accountBankInfoState?.let {
            userName = it.userName
            bankCode = it.bankCode
            cardHolderName = it.cardHolderName
            bankName = it.bankName
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = currentRoute ?: NavigationItem.DefaultItems.first().route,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        topBar = {
            CustomHeader(
                title = "Bank Information",
                onBackClick = { navController.popBackStack() },
                backgroundColor = Color(0xFF3498DB),
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(mainColor),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF4FFF9))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    OutlinedTextField(
//                        value = userName,
//                        onValueChange = { userName = it },
//                        label = { Text("User Name") },
//                        modifier = Modifier.fillMaxWidth(),
//                        enabled = false
//                    )
                    OutlinedTextField(
                        value = bankCode,
                        onValueChange = { bankCode = it },
                        label = { Text("Bank Code") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                    OutlinedTextField(
                        value = cardHolderName,
                        onValueChange = { cardHolderName = it },
                        label = { Text("Card Holder Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Bank Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Account Balance:",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    // Hiển thị số dư từ accountBankInfoState
                    Text(
                        text = formatter.format(accountBankInfoState?.balance ?: 0.0),
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = transferAmountStr,
                        onValueChange = { transferAmountStr = it.filter { char -> char.isDigit() } },
                        label = { Text("Transfer Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val amount = transferAmountStr.toDoubleOrNull() ?: 0.0
                            bankViewModel.transferFromBankToAccount(amount) { success, message -> // Gọi hàm mới
                                if (success) {
                                    Toast.makeText(context, "Transfer successful!", Toast.LENGTH_SHORT).show()
                                    bankViewModel.refreshAccountBankInfo() // Cập nhật lại số dư BankInfo
                                    bankViewModel.refreshAccount() // Cập nhật lại số dư Account
                                    transferAmountStr = "" // Clear input field
                                } else {
                                    Toast.makeText(context, "Transfer failed: $message", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Transfer to Account")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomNumberPad(
    onNumberClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            (1..3).forEach { number ->
                Button(onClick = { onNumberClick(number) }) { Text(number.toString(), fontSize = 24.sp) }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            (4..6).forEach { number ->
                Button(onClick = { onNumberClick(number) }) { Text(number.toString(), fontSize = 24.sp) }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            (7..9).forEach { number ->
                Button(onClick = { onNumberClick(number) }) { Text(number.toString(), fontSize = 24.sp) }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { onDeleteClick() }) { Text("Delete", fontSize = 24.sp) }
            Button(onClick = { onNumberClick(0) }) { Text("0", fontSize = 24.sp) }
            Spacer(modifier = Modifier.weight(1f)) // Để nút "Xóa" và "0" căn giữa
        }
    }
}