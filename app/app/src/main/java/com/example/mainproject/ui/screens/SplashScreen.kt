package com.example.mainproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mainproject.R
import androidx.compose.ui.res.colorResource
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToMain: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        onNavigateToMain()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.mainColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_app_1),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
        }
    }
}


