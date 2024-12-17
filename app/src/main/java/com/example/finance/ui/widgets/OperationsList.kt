package com.example.finance.ui.widgets

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.finance.model.data.DebtOperations
import com.example.finance.model.supabase.SupabaseHelper
import kotlinx.coroutines.launch

@Composable
fun OperationsList (
    id: String,
    sharedPreferences: SharedPreferences
) {
    val coroutineScope = rememberCoroutineScope()
    val supabaseHelper = remember { SupabaseHelper(sharedPreferences) }
    var operations by remember { mutableStateOf(emptyList<DebtOperations>()) }

    LaunchedEffect(id) {
        coroutineScope.launch {
            try {
                operations = supabaseHelper.fetchUserDebtsOperations(id)
            } catch (e: Exception) {
                Log.e("OperationsList", "Error fetching operations: ${e.localizedMessage}")
            }
        }
    }

    if (operations.isEmpty()) {
        Text(
            text = "У Вас ни одной совершенной операции",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(operations.size) { index ->
                OperationBox(
                    operation = operations[index],
                    sharedPreferences = sharedPreferences
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}