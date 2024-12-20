import android.content.SharedPreferences
import android.util.Log
import android.widget.Space
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.finance.model.data.DebtOperations
import com.example.finance.model.supabase.SupabaseHelper
import com.example.finance.ui.widgets.OperationBox
import kotlinx.coroutines.launch

@Composable
fun OperationsList(
    id: String,
    sharedPreferences: SharedPreferences
) {
    val coroutineScope = rememberCoroutineScope()
    val supabaseHelper = remember { SupabaseHelper(sharedPreferences) }
    var operations by remember { mutableStateOf(emptyList<DebtOperations>()) }
    var sortedOperations by remember { mutableStateOf(emptyList<DebtOperations>()) }

    var isDateDescending by remember { mutableStateOf(true) }
    var isAmountDescending by remember { mutableStateOf(true) }

    LaunchedEffect(id) {
        coroutineScope.launch {
            try {
                operations = supabaseHelper.fetchUserDebtsOperations(id)
                sortedOperations = operations
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Сортировка",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                    isDateDescending = !isDateDescending
                    sortedOperations = if (isDateDescending) {
                        operations.sortedByDescending { it.createdAt }
                    } else {
                        operations.sortedBy { it.createdAt }
                    }
                }) {
                    Text(text = if (isDateDescending) "Дата ↓" else "Дата ↑")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                    isAmountDescending = !isAmountDescending
                    sortedOperations = if (isAmountDescending) {
                        operations.sortedByDescending { it.amount }
                    } else {
                        operations.sortedBy { it.amount }
                    }
                }) {
                    Text(text = if (isAmountDescending) "Депозит ↓" else "Депозит ↑")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                contentPadding = PaddingValues(vertical = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(sortedOperations.size) { index ->
                    OperationBox(
                        operation = sortedOperations[index],
                        sharedPreferences = sharedPreferences
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}
