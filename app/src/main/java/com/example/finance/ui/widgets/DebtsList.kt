package com.example.finance.ui.widgets

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.finance.model.data.Debt
import com.example.finance.model.supabase.SupabaseHelper
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DebtsList(
    id: String,
    sharedPreferences: SharedPreferences
) {
    val coroutineScope = rememberCoroutineScope()
    val supabaseHelper = remember { SupabaseHelper(sharedPreferences) }
    var debts by remember { mutableStateOf(emptyList<Debt>()) }
    var filteredDebts by remember { mutableStateOf(emptyList<Debt>()) }
    var selectedType by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        coroutineScope.launch {
            try {
                debts = supabaseHelper.fetchUserDebtsData(id)
                filteredDebts = debts
            } catch (e: Exception) {
                Log.e("DebtsList", "Error fetching debts: ${e.localizedMessage}")
            }
        }
    }

    if (debts.isEmpty()) {
        Text(
            text = "Занимаемых средств нет",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                    selectedType = "Долг"
                    filteredDebts = debts.filter { it.debtType == 3 }
                }) {
                    Text(text = "Долг")
                }
                Button(
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                    selectedType = "Ипотека"
                    filteredDebts = debts.filter { it.debtType == 1 }
                }) {
                    Text(text = "Ипотека")
                }
                Button(
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                    selectedType = "Кредит"
                    filteredDebts = debts.filter { it.debtType == 2 }
                }) {
                    Text(text = "Кредит")
                }
                Button(
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                    selectedType = null
                    filteredDebts = debts
                }) {
                    Text(text = "Все")
                }
            }

            if (filteredDebts.isEmpty()) {
                Text(
                    text = "Нет данных для выбранного типа",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                VerticalPager(
                    count = filteredDebts.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(2.dp),
                    contentPadding = PaddingValues(vertical = 2.dp),
                    itemSpacing = 8.dp,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { pageIndex ->
                    DebtBox(debt = filteredDebts[pageIndex], sharedPreferences)
                }
            }
        }
    }
}