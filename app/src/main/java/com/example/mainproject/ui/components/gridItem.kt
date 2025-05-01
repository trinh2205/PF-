
//package com.example.mainproject.ui.components
//
//import android.graphics.Color
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color as ComposeColor // Alias for Compose Color
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.foundation.background
//import androidx.compose.ui.graphics.vector.ImageVector
//
//@Composable
//fun GridItem(
//    id: Int,
//    text: String,
//    sizeItem: Dp,
//    colorText: ComposeColor, // Đã sửa thành ComposeColor
//    colorBackground: ComposeColor, // Đã sửa thành ComposeColor
//    activeTextColor: ComposeColor, // Đã sửa thành ComposeColor
//    activeBackgroundColor: ComposeColor, // Đã sửa thành ComposeColor
//    roundedCorner: Int,
//    iconType: String, // Thêm tham số để nhận loại icon
//    categoryIcons: Map<String, ImageVector>, // Thêm tham số để nhận map ánh xạ icon
//    onClick: (Int) -> Unit
//) {
//    var isActive by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = Modifier
//            .size(sizeItem)
//            .clip(RoundedCornerShape(roundedCorner.dp))
//            .background(if (isActive) activeBackgroundColor else colorBackground)
//            .clickable {
//                isActive = !isActive
//                onClick(id)
//            },
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = text,
//            style = TextStyle(
//                color = if (isActive) activeTextColor else colorText,
//                textAlign = TextAlign.Center
//            )
//        )
//    }
//}

package com.example.mainproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GridItem(
    id: String,
    text: String,
    sizeItem: Dp,
    colorText: Color,
    colorBackground: Color,
    activeTextColor: Color,
    activeBackgroundColor: Color,
    roundedCorner: Int,
    iconType: String,
    categoryIcons: Map<String, ImageVector>,
    onClick: (String) -> Unit
) {
    val icon = categoryIcons[iconType] ?: Icons.Filled.Fastfood
    Box(
        modifier = Modifier
            .size(sizeItem)
            .clip(RoundedCornerShape(roundedCorner.dp))
            .background(colorBackground)
            .clickable { onClick(id) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = colorText,
            modifier = Modifier.size(40.dp)
        )
    }
}
