package com.example.finance

import ProfileScreen
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.FinanceTheme
import com.example.finance.model.data.TabBarItem
import com.example.finance.ui.screens.OperationsScreen
import com.example.finance.ui.screens.DebtScreen
import com.example.finance.ui.widgets.TabView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class DebtActivity : ComponentActivity() {
    private lateinit var email: String
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("FinanceAppPreferences", Context.MODE_PRIVATE)
        enableEdgeToEdge()
        setContent {
            val homeTab = TabBarItem(
                title = stringResource(id = R.string.MAIN),
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            )
            val debtTab = TabBarItem(
                title = stringResource(id = R.string.OPERATIONS),
                selectedIcon = Icons.AutoMirrored.Filled.List,
                unselectedIcon = Icons.AutoMirrored.Outlined.List
            )

            val tabBarItems = listOf(homeTab, debtTab)

            val navController = rememberNavController()

            FinanceTheme {
                val systemUiController = rememberSystemUiController()

                systemUiController.isSystemBarsVisible = true
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    bottomBar = {
                        TabView(tabBarItems, navController)
                    }
                ) {
                    NavHost(navController = navController, startDestination = homeTab.title) {
                        composable(homeTab.title) {
                            DebtScreen(
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                goToDebtScreen = {
                                    navController.navigate(debtTab.title)
                                },
                                sharedPreferences
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                onBackClick = { navController.popBackStack() },
                                onExitAccount = {
                                    sharedPreferences.edit()
                                        .putBoolean("isLogin", false)
                                        .apply()

                                    startActivity(Intent(this@DebtActivity, MainActivity::class.java))
                                },
                                sharedPreferences
                            )
                        }
                        composable(debtTab.title) {
                            OperationsScreen(sharedPreferences)
                        }
                    }
                }
            }
        }
    }
}