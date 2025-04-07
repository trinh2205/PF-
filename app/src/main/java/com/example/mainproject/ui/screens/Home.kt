package com.example.mainproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Preview(showBackground = true)
@Composable

fun Home() {
    val textField1 = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .background(
                color = Color(0xFFFFFFFF),
            )
    ) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(40.dp))
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = Color(0xFF3498DB),
                    shape = RoundedCornerShape(40.dp)
                )
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
            }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp,start = 38.dp,end = 38.dp,)
            ) {
                Column(
                    modifier = Modifier
                        .padding(end = 12.dp,)
                        .weight(1f)
                ){
                    Text("Hi, Welcome Back",
                        color = Color(0xFF052224),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Good Morning",
                        color = Color(0xFF052224),
                        fontSize = 14.sp,
                    )

                }
                CoilImage(
                    imageModel = {"https://img.icons8.com/?size=100&id=LoDgQVjdqIpy&format=png&color=000000"},
                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(40.dp))
                        .width(30.dp)
                        .height(30.dp)
                )

            }
        }
    }
}
