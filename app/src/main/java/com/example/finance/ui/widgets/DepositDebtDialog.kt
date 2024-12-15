package com.example.finance.ui.widgets

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
                            supabaseHelper.updatePaidData(
                                id = debtId,
                                deposit = deposit.toDouble()
                            )
                            supabaseHelper.fetchUserDebtsData(userId)
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