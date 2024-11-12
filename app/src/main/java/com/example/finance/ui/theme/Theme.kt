package com.example.finance.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CustomDarkPrimary,
    secondary = CustomDarkSecondary,
    onPrimary = CustomDarkOnPrimary,
    background = CustomDarkPrimary,
    surface = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CustomLightPrimary,       // Основной цвет интерфейса для заголовков, кнопок и элементов выделения
    onPrimary = CustomLightOnPrimary,   // Цвет текста/иконок, располагающихся поверх primary (контрастный цвет)
    secondary = CustomLightSecondary,   // Дополнительный цвет для второстепенных элементов интерфейса
    background = CustomLightPrimary,    // Основной цвет фона приложения
    surface = Color.White,              // Цвет поверхности для карточек, диалогов и прочих поверхностей, отличающихся от фона
    onBackground = Color.White,         // Цвет текста/иконок на основном фоне background
    onSurface = Color.White             // Цвет текста/иконок, располагающихся поверх surface
)

@Composable
fun FinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}