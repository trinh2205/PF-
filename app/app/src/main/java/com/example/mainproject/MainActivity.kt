package com.example.mainproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.mainproject.NAVIGATION.AppNavigation
import com.example.mainproject.ui.theme.MainProjectTheme
import com.google.firebase.auth.FirebaseAuth

//import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MainProjectTheme {
                AppNavigation(auth = auth, navController = navController)
            }
        }
    }
}