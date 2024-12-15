package com.example.finance.ui.widgets

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.finance.model.data.Debt
import com.example.finance.model.supabase.SupabaseHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

@OptIn(ExperimentalWearMaterialApi::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun DebtBox(debt: Debt, sharedPreferences: SharedPreferences) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val density = LocalDensity.current.density
    val swipeThreshold = with(LocalDensity.current) { 20.dp.toPx() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDepositDialog by remember { mutableStateOf(false) }
    var showPaidAmount by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(showDeleteDialog) {
        swipeableState.snapTo(0)
    }

    LaunchedEffect(showUpdateDialog) {
        swipeableState.snapTo(0)
    }

    val swipeableModifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
        .padding(8.dp)
        .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
        .swipeable(
            state = swipeableState,
            anchors = mapOf(0f to 0, -swipeThreshold to 1, swipeThreshold to 2),
            orientation = Orientation.Horizontal
        )

    when {
        swipeableState.offset.value < -swipeThreshold -> showDeleteDialog = true
        swipeableState.offset.value > swipeThreshold -> showUpdateDialog = true
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удаление записи") },
            text = { Text("Вы уверены, что хотите удалить эту запись?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            SupabaseHelper(sharedPreferences = sharedPreferences).deleteDebt(debt.id)
                        }
                        showDeleteDialog = !showDeleteDialog
                        coroutineScope.launch {
                            SupabaseHelper(sharedPreferences = sharedPreferences)
                                .fetchUserDebtsData(debt.userId)
                        }
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Update Debt Dialog
    if (showUpdateDialog) {
        UpdateDebtDialog(
            onDismiss = { showUpdateDialog = false },
            debtId = debt.id,
            userId = debt.userId,
            sharedPreferences = sharedPreferences
        )
    }

    if (showDepositDialog) {
        DepositDebtDialog(
            onDismiss = { showDepositDialog = false },
            debtId = debt.id,
            userId = debt.userId,
            sharedPreferences = sharedPreferences
        )
    }

    Card(
        modifier = swipeableModifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateString = debt.returnDate
                val currentFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val date = currentFormat.parse(dateString) ?: ""
                val targetFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val formattedDate = targetFormat.format(date)
                Column {
                    Text(
                        "ID: ${debt.id}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(formattedDate, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(debt.title!!, style = MaterialTheme.typography.titleMedium)
                }
                Column {
                    Text("${debt.amount} ₽", style = MaterialTheme.typography.titleLarge)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { (debt.paid / debt.amount).toFloat() },
                    modifier = Modifier
                        .width(150.dp)
                        .height(20.dp)
                        .clickable { showPaidAmount = true }
                )
                if (debt.interestRate != null) {
                    Text(
                        "${debt.interestRate.toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            showDepositDialog = true
                        }
                    },
                    modifier = Modifier
                        .height(30.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Оплатить")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (showPaidAmount) {
                Text(
                    text = "Оплачено: ${debt.paid} ₽",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            val debtTypeName = when (debt.debtType) {
                1 -> "Ипотека"
                2 -> "Кредит"
                3 -> "Долг"
                else -> ""
            }
            Text(text = debtTypeName, style = MaterialTheme.typography.titleMedium)
        }
    }
}