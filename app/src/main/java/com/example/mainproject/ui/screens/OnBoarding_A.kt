package com.example.mainproject.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import com.example.mainproject.R
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.colorResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Onboarding() {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope() // Tạo CoroutineScope gắn với vòng đời của Composable
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding,
            description = "Welcome to Finance Manager App"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_other,
            description = "Are you ready to take control of your finaces?"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.buttonColor)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to the Financial Management",
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
            color = colorResource(id = R.color.textColor)
        )

        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        PagerIndicator(pages.size, pagerState.currentPage)

        Button(
            onClick = {
                coroutineScope.launch {
                    if (pagerState.currentPage < pages.size - 1) {
                        pagerState.scrollToPage(pagerState.currentPage + 1)
                    } else {
                        // Xử lý khi hoàn thành onboarding
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(color = colorResource(id = R.color.textColor), text = if (pagerState.currentPage < pages.size - 1) "Next" else "Get Started")
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = page.description, fontSize = 16.sp)
    }
}

@Composable
fun PagerIndicator(pageCount: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        repeat(pageCount) { index ->
            RadioButton(
                selected = index == currentPage,
                onClick = null, // Vô hiệu hóa khả năng nhấp trực tiếp
                colors = RadioButtonDefaults.colors(
                    selectedColor = colorResource(id = R.color.mainColor),
                    unselectedColor = colorResource(id = R.color.textColor)
                )
            )
        }
    }
}

data class OnboardingPage(
    val imageRes: Int,
    val description: String
)
