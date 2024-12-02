package com.example.finance

import ProfileScreen
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.compose.FinanceTheme
import com.example.finance.model.data.Debt
import com.example.finance.model.data.User
import com.example.finance.model.supabase.SupabaseHelper
import com.example.finance.ui.screens.AddDebtScreen
import com.example.finance.ui.screens.DebtScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.ktor.utils.io.concurrent.shared
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

data class TabBarItem (
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class DebtActivity : ComponentActivity() {
    private lateinit var email: String
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("FinanceAppPreferences", Context.MODE_PRIVATE)
        enableEdgeToEdge()
        setContent {
            val homeTab = TabBarItem(
                title = "Главная",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            )
            val debtTab = TabBarItem(
                title = "Долги",
                selectedIcon = Icons.AutoMirrored.Filled.List,
                unselectedIcon = Icons.AutoMirrored.Outlined.List
            )

            val tabBarItems = listOf(homeTab, debtTab)

            val navController = rememberNavController()

            FinanceTheme {
                val systemUiController = rememberSystemUiController()

                systemUiController.isSystemBarsVisible = true
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    bottomBar = {
                        TabView(tabBarItems, navController)
                    }
                ) {
                    NavHost(navController = navController, startDestination = homeTab.title) {
                        composable(homeTab.title) {
                            DebtScreen(
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                goToDebtScreen = {
                                    navController.navigate(debtTab.title)
                                },
                                sharedPreferences
                            )
                        }
                        composable("profile") {
                            ProfileScreen(onBackClick = { navController.popBackStack() },
                                sharedPreferences)
                        }
                        composable(debtTab.title) {
                            AddDebtScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    NavigationBar {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = { Text(
                    tabBarItem.title,
                    style = MaterialTheme.typography.labelLarge
                ) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}

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

                    Button(onClick = { showDatePicker.value = true }) {
                        Text("Открыть выбор даты")
                    }
                }

            }
        },
        confirmButton = {
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
                            Log.e("AddDebtDialog", "Error adding debt: ${e.localizedMessage}")
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


@OptIn(ExperimentalPagerApi::class)
@Composable
fun DebtsList(id: String, sharedPreferences: SharedPreferences) {
    val coroutineScope = rememberCoroutineScope()
    val supabaseHelper = remember { SupabaseHelper(sharedPreferences) }
    var debts by remember { mutableStateOf(emptyList<Debt>()) }

    LaunchedEffect(id) {
        coroutineScope.launch {
            try {
                debts = supabaseHelper.fetchUserDebtsData(id)
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
        VerticalPager(
            count = debts.size,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(2.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
            itemSpacing = 8.dp
        ) { pageIndex ->
            DebtBox(debt = debts[pageIndex], sharedPreferences)
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun DebtBox(debt: Debt, sharedPreferences: SharedPreferences) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val density = LocalDensity.current.density
    val swipeThreshold = with(LocalDensity.current) { 20.dp.toPx() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    if (!showDeleteDialog) {
        LaunchedEffect(showDeleteDialog) {
            swipeableState.snapTo(0)
        }
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
        Toast.makeText(LocalContext.current, "Ну допустим изменение работает", Toast.LENGTH_SHORT).show()
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
                )
                Button(
                    onClick = {
                        // Handle payment logic here
                    },
                    modifier = Modifier
                        .height(30.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Оплатить")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            val debtTypeName = when (debt.debtType) {
                1 -> "Ипотека"
                2 -> "Кредит"
                3 -> "Долг"
                else -> ""
            }
            Text(text = "$debtTypeName", style = MaterialTheme.typography.titleMedium)
        }
    }
}


