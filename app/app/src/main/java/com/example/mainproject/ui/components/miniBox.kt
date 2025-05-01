package com.example.mainproject.ui.components // Đảm bảo package này phù hợp với vị trí file của bạn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.AutoMirrored.Rounded
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier // Import Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainproject.R

@Composable
fun miniBox(
    icon: ImageVector? = null,
    title: String,
    money: Double,
    colorBG: Color,
    colorText: Color,
    fillWidth: Dp = Dp.Unspecified,
    fillHeight: Dp = Dp.Unspecified,
    modifier: Modifier = Modifier, // Thêm tham số modifier với giá trị mặc định
    onClick: () -> Unit = {}, // Thêm tham số onClick
    titleFontSize: TextUnit = 14.sp, // Thêm tham số cho kích thước chữ tiêu đề
    titleFontWeight: FontWeight = FontWeight.Normal, // Thêm tham số cho độ đậm chữ tiêu đề
    moneyFontSize: TextUnit = 24.sp, // Thêm tham số cho kích thước chữ số tiền
    moneyFontWeight: FontWeight = FontWeight.Bold, // Thêm tham số cho độ đậm chữ số tiền
    Rounded: Dp = 16.dp,
    isActive: Boolean = false,
    activeBackgroundColor: Color = colorResource(id = R.color.activeBGColor), // Màu nền khi active
    activeIconColor: Color = Color.White, // Màu icon khi active
    activeTextColor: Color = Color.White // Màu chữ khi active
) {
    val backgroundColor = if (isActive) activeBackgroundColor else colorBG
    val currentIconColor = if (isActive) activeIconColor else colorText
    val currentTextColor = if (isActive) activeTextColor else colorText
    Box(
        modifier = modifier // Sử dụng modifier được truyền vào
            .width(fillWidth)
            .height(fillHeight)
            .background(backgroundColor, RoundedCornerShape(Rounded))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = colorText,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(text = title, color = colorText,fontSize = titleFontSize, // Sử dụng kích thước chữ tiêu đề
                fontWeight = titleFontWeight)// Sử dụng độ đậm chữ tiêu đề)
            Text(text = money.toString(), color = colorText, fontSize = moneyFontSize, // Sử dụng kích thước chữ tiêu đề
                fontWeight = moneyFontWeight) // Sử dụng độ đậm chữ tiêu đề)
        }
    }
}