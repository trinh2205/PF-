package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mainproject.NAVIGATION.Routes
import com.example.mainproject.ui.components.CustomHeader
import com.example.mainproject.viewModel.ChatbotViewModel

data class Message(val text: String, val isUser: Boolean)

@Composable
fun ChatBotScreen(navController: NavController) {
    val chatbotViewModel: ChatbotViewModel = viewModel(factory = ChatbotViewModel.provideFactory())
    val messages = remember { mutableStateListOf<Message>() }
    var inputText by remember { mutableStateOf("") }
    val chatBotName = "ChatBot"
    val chatBotColor = Color(0xFF4CAF50)
    val userColor = Color(0xFF2196F3)
    val backgroundColor = Color(0xFFF0F0F0)
    val headerBackgroundColor = Color(0xFF3498DB)
    val headerContentColor = Color.White

    val botResponse by chatbotViewModel.chatbotResponse.collectAsState()
    val isBotLoading by chatbotViewModel.isLoading.collectAsState()

    LaunchedEffect(botResponse) {
        botResponse?.let { response ->
            messages.add(Message(response, false))
            chatbotViewModel.clearResponse() // Reset response state sau khi hiển thị
        }
    }

    Scaffold(
        topBar = {
            CustomHeader(
                title = chatBotName,
                onBackClick = { navController.popBackStack() },
                backgroundColor = headerBackgroundColor,
                contentColor = headerContentColor,
                onNotificationClick = {
                    navController.navigate(route = Routes.NOTIFICATION)
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Nhập tin nhắn") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = headerBackgroundColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = headerBackgroundColor, // Màu label khi focus (tùy chọn)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userMessage = Message(inputText, true)
                            messages.add(userMessage)
                            chatbotViewModel.getResponse(inputText) // Gọi API để lấy phản hồi
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = headerBackgroundColor,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Gửi")
                }
            }
        },
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        backgroundColor = if (message.isUser) userColor else chatBotColor,
                        shape = RoundedCornerShape(8.dp),
                        elevation = 1.dp
                    ) {
                        Text(
                            text = message.text,
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
            item {
                if (isBotLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

// Hàm giả lập phản hồi đã được loại bỏ.