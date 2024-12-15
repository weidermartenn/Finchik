package com.example.finance.ui.errorhandling

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
fun handleSupabaseError(exception: Exception): String {
    return when (exception) {
        is HttpException -> "Сетевая ошибка. Проверьте подключение к интернету."
        is IllegalArgumentException -> "Некорректные данные. Проверьте введённые поля."
        else -> "Произошла неизвестная ошибка: ${exception.localizedMessage}"
    }
}