package com.example.mainproject.ui.screens

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.example.mainproject.ui.components.BottomNavigationBar

@Composable
fun ItemScreen(){
    Scaffold(bottomBar = {
        BottomNavigationBar(
            selectedItem = selectedTab,
            onItemClick = { newItem -> selectedTab = newItem.route }
        )
    }) {

    }
}