package com.example.mainproject.mainproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconWithCircleBackground(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    iconColor: Color = Color.White,
    circleColor: Color = Color.Red,
    circleSize: Dp = 36.dp, // Kích thước của vòng tròn
    iconPadding: Dp = 6.dp // Khoảng cách giữa icon và vòng tròn
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(circleSize)
            .background(color = circleColor, shape = CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.padding(iconPadding)
        )
    }
}