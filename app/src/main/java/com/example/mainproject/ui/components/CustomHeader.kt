package com.example.mainproject.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainproject.R
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CustomHeader(
    title: String,
    onBackClick: () -> Unit,
    hasNotifications: StateFlow<Boolean>? = null, // Sử dụng StateFlow để theo dõi thông báo động
    onNotificationClick: () -> Unit = {},
    backgroundColor: Color,
    contentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .background(color = backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Nút Quay lại
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // Tiêu đề
        Text(
            text = title,
            fontSize = 20.sp,
            color = contentColor
        )

        // Nút Thông báo
        Box(contentAlignment = Alignment.CenterEnd) {
            IconButton(onClick = onNotificationClick) {
                if (hasNotifications != null) {
                    val hasNewNotification by hasNotifications.collectAsState(initial = false)
                    if (hasNewNotification) {
                        AnimatedIcon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Thông báo mới",
                            tint = colorResource(R.color.mainColor_other),
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        IconWithCircleBackground(
                            icon = Icons.Default.Notifications,
                            contentDescription = "Thông báo",
                            circleColor = colorResource(R.color.mainColor_other),
                            circleSize = 36.dp,
                            iconPadding = 6.dp
                        )
                    }
                } else {
                    // Hiển thị icon thông báo tĩnh với vòng tròn background
                    IconWithCircleBackground(
                        icon = Icons.Default.Notifications,
                        contentDescription = "Thông báo",
                        circleColor = colorResource(R.color.mainColor_other),
                        circleSize = 36.dp,
                        iconPadding = 6.dp,
                        iconColor = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val alpha by androidx.compose.animation.core.rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 500
                1f at 0
                0.5f at 250
                1f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint.copy(alpha = alpha),
        modifier = modifier
    )
}