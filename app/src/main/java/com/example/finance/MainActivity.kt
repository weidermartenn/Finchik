// MainActivity.kt
package com.example.finance

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.animation.doOnEnd
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.compose.FinanceTheme
import com.example.finance.ui.screens.DebtScreen
import com.example.finance.ui.screens.LoginScreen
import com.example.finance.ui.screens.RegisterScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_X,
                0f,
                -splashScreenView.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 500L

            slideUp.doOnEnd { splashScreenView.remove() }

            slideUp.start()
        }
        enableEdgeToEdge()
        setContent {
            FinanceTheme {
                val navController = rememberNavController()
                val systemUiController = rememberSystemUiController()

                systemUiController.isSystemBarsVisible = true
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login_screen",
                        Modifier.padding(innerPadding)
                    ) {
                        composable("login_screen") {
                            LoginScreen(
                                onLoginClick = { _, _ ->
                                    startActivity(
                                        Intent(this@MainActivity,
                                            DebtActivity::class.java)
                                    )
                                },
                                onRegisterClick = {
                                    navController.navigate("register_screen")
                                }
                            )
                        }
                        composable("register_screen") {
                            RegisterScreen(
                                onRegisterComplete = { _, _ ->
                                    // Логика завершения регистрации
                                    navController.popBackStack() // Возвращение к экрану логина
                                },
                                backToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}

