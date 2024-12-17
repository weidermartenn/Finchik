package com.example.finance.ui.widgets

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.finance.R
import com.example.finance.model.supabase.SupabaseHelper
import com.example.finance.ui.errorhandling.ShowErrorDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositDebtDialog(
    onDismiss: () -> Unit,
    debtId: String,
    userId: String,
    sharedPreferences: SharedPreferences
) {
    var deposit by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val supabaseHelper = remember { SupabaseHelper(sharedPreferences) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Оплата") },
        text = {
            Column {
                OutlinedTextField(
                    value = deposit,
                    onValueChange = { deposit = it },
                    label = { Text("Размер оплаты") }
                )
            }
        },
        confirmButton = {
            if (errorMessage != null) {
                ShowErrorDialog(errorMessage!!) {
                    errorMessage = null
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val depositAmount = deposit.toDouble()

                            // Обновление данных в Supabase
                            supabaseHelper.updatePaidData(
                                id = debtId,
                                deposit = depositAmount
                            )
                            supabaseHelper.fetchUserDebtsData(userId)

                            // Отправка уведомления
                            sendNotification(
                                context = context,
                                debtId = debtId,
                                amount = depositAmount
                            )

                            onDismiss()
                        } catch (e: Exception) {
                            errorMessage = e.localizedMessage ?: "Неизвестная ошибка"
                        }
                    }
                }
            ) {
                Text("Оплатить")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Отмена")
            }
        }
    )
}

@SuppressLint("ObsoleteSdkInt")
fun sendNotification(context: Context, debtId: String, amount: Double) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    val notificationId = 1
    val channelId = "debt_operations_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Операции по долгам"
        val descriptionText = "Уведомления о платежах по долгам"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.dm_logo)
        .setContentTitle("Платеж успешно выполнен")
        .setContentText("Вы оплатили $amount руб. (Долг ID: $debtId)")
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("Платеж на сумму $amount руб. был выполнен успешно. Долг ID: $debtId.")
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notificationBuilder.build())
    }
}
