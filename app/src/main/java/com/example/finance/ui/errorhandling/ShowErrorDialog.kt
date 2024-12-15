package com.example.finance.ui.errorhandling

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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