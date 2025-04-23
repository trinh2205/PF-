package com.example.mainproject.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainproject.R
import kotlinx.coroutines.launch

@Composable
fun Onboarding() {
    val pagerState = rememberPagerState(pageCount = { 2 }) // Khởi tạo pagerState với số trang
    val coroutineScope = rememberCoroutineScope() // Tạo CoroutineScope cho Composable
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding,
            description = "Chào mừng bạn đến với ứng dụng Quản lý Tài chính"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_other,
            description = "Bạn đã sẵn sàng kiểm soát tài chính của mình chưa?"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.buttonColor)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Chào mừng đến với Quản lý Tài chính",
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
            color = colorResource(id = R.color.textColor)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        PagerIndicator(pageCount = pages.size, currentPage = pagerState.currentPage)

        Button(
            onClick = {
                coroutineScope.launch {
                    if (pagerState.currentPage < pages.size - 1) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        // Xử lý khi hoàn thành onboarding (ví dụ: chuyển sang màn hình chính)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                color = colorResource(id = R.color.textColor),
                text = if (pagerState.currentPage < pages.size - 1) "Tiếp theo" else "Bắt đầu"
            )
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

// Lớp Activity để gọi Composable
class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Onboarding()
        }
    }
}