// MainActivity.kt
package com.example.finance

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.http.HttpException
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.compose.FinanceTheme
import com.example.finance.model.data.User
import com.example.finance.ui.screens.DebtScreen
import com.example.finance.ui.screens.LoginScreen
import com.example.finance.ui.screens.RegisterScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.example.finance.model.supabase.SupabaseHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var supabaseHelper: SupabaseHelper
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация SharedPreferences
        sharedPreferences = getSharedPreferences("FinanceAppPreferences", Context.MODE_PRIVATE)

        // Передача SharedPreferences в SupabaseHelper
        supabaseHelper = SupabaseHelper(sharedPreferences)

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
                            var errorMessage by remember { mutableStateOf<String?>(null) }

                            LoginScreen(
                                onLoginClick = { email, password ->
                                    lifecycleScope.launch {
                                        try {
                                            supabaseHelper.signInWithEmail(email, password)
                                            val intent = Intent(this@MainActivity, DebtActivity::class.java)
                                            startActivity(intent)
                                            Toast.makeText(
                                                this@MainActivity,
                                                R.string.AUTHORIZATION_COMPLETE,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } catch (e: Exception) {
                                            errorMessage = e.localizedMessage ?: "Неизвестная ошибка"
                                        }
                                    }
                                },
                                onRegisterClick = {
                                    navController.navigate("register_screen")
                                },
                                sharedPreferences
                            )

                            errorMessage?.let { message ->
                                ShowErrorDialog(message) {
                                    errorMessage = null
                                }
                            }
                        }
                        composable("register_screen") {
                            var errorMessage by remember { mutableStateOf<String?>(null) }

                            RegisterScreen(
                                onRegisterComplete = { email, username, password ->
                                    lifecycleScope.launch {
                                        try {
                                            supabaseHelper.signUpWithEmail(email, username, password)
                                            navController.popBackStack()
                                            Toast.makeText(
                                                this@MainActivity,
                                                R.string.REGISTRATION_COMPLETE,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } catch (e: Exception) {
                                            errorMessage = e.localizedMessage ?: "Неизвестная ошибка"
                                        }
                                    }
                                },
                                backToLogin = {
                                    navController.popBackStack()
                                }
                            )

                            errorMessage?.let { message ->
                                ShowErrorDialog(message) {
                                    errorMessage = null
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
fun handleSupabaseError(exception: Exception): String {
    return when (exception) {
        is HttpException -> "Сетевая ошибка. Проверьте подключение к интернету."
        is IllegalArgumentException -> "Некорректные данные. Проверьте введённые поля."
        else -> "Произошла неизвестная ошибка: ${exception.localizedMessage}"
    }
}

@Composable
fun ShowErrorDialog(message: String, onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("ОК")
            }
        },
        title = { Text("Ошибка") },
        text = { Text(message) }
    )
}

