package com.example.finance.ui.widgets

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.example.finance.model.data.DebtOperations
import java.text.SimpleDateFormat

@Composable
fun OperationBox(operation: DebtOperations, sharedPreferences: SharedPreferences) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "ID З.С.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    operation.debtId,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            val dateString = operation.createdAt
            val currentFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date = currentFormat.parse(dateString) ?: ""
            val targetFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
            val formattedDate = targetFormat.format(date)
            Text(formattedDate)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Внесенная оплата: ${operation.amount}")
        }
    }
}