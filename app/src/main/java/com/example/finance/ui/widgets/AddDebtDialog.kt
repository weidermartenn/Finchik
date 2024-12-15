package com.example.finance.ui.widgets

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.finance.model.supabase.SupabaseHelper
import com.example.finance.ui.errorhandling.ShowErrorDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtDialog(
    onDismiss: () -> Unit,
    userId: String,
    sharedPreferences: SharedPreferences
) {
    val debtName = remember { mutableStateOf("") }
    val paymentAmount = remember { mutableStateOf("") }
    val selectedDebtType = remember { mutableStateOf("") }
    val interestRate = remember { mutableStateOf("") }
    val selectedDate = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val supabaseHelper = remember { SupabaseHelper(sharedPreferences) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Добавить долг") },
        text = {
            Column {
                OutlinedTextField(
                    value = debtName.value,
                    onValueChange = { debtName.value = it },
                    label = { Text("Название долга") }
                )
                OutlinedTextField(
                    value = paymentAmount.value,
                    onValueChange = { paymentAmount.value = it },
                    label = { Text("Сумма долга") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Выберите тип займа:")
                Column {
                    listOf("Ипотека" to 1, "Кредит" to 2, "Долг" to 3).forEach { (option, value) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDebtType.value == option,
                                onClick = { selectedDebtType.value = option }
                            )
                            Text(option)
                        }
                    }
                }
                if (selectedDebtType.value == "Ипотека" || selectedDebtType.value == "Кредит") {
                    OutlinedTextField(
                        value = interestRate.value,
                        onValueChange = { interestRate.value = it },
                        label = { Text("Процентная ставка") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                if (selectedDebtType.value.isNotEmpty()) {
                    val datePickerState = remember { DatePickerState(locale = Locale("ru", "RU")) }
                    val showDatePicker = remember { mutableStateOf(false) }

                    if (showDatePicker.value) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker.value = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let {
                                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            selectedDate.value = formatter.format(it)
                                        }
                                        showDatePicker.value = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker.value = false }) {
                                    Text("Отмена")
                                }
                            }
                        ) {
                            DatePicker(
                                state = datePickerState,
                                title = {
                                    Text(
                                        "Выберите дату",
                                        modifier = Modifier
                                            .padding(6.dp),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                },
                                headline = {
                                    Text(
                                        "Выбранная дата: ${selectedDate.value}",
                                        modifier = Modifier
                                            .padding(6.dp),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                },
                                showModeToggle = true
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = { showDatePicker.value = true }) {
                        Text("Открыть выбор даты")
                    }
                }

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
                            val debtData = listOf(
                                debtName.value,
                                paymentAmount.value,
                            )

                            val validDate = if (selectedDate.value.isEmpty()) null else selectedDate.value
                            supabaseHelper.addDebtToDatabase(
                                id = userId,
                                title = debtName.value,
                                amount = paymentAmount.value.toDouble(),
                                debtType = selectedDebtType.value,
                                interestRate = interestRate.value.toDoubleOrNull(),
                                returnDate = selectedDate.value ?: ""
                            )
                            supabaseHelper.fetchUserDebtsData(userId)
                            onDismiss()
                        } catch (e: Exception) {
                            errorMessage = e.localizedMessage ?: "Неизвестная ошибка"
                        }
                    }
                }
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Отмена")
            }
        }
    )
}