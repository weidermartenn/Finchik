package com.example.finance

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.finance.model.supabase.SupabaseHelper
import com.example.finance.ui.screens.AddDebtScreen
import com.example.finance.ui.screens.DebtScreen
import com.example.finance.ui.screens.ProfileScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class TabBarItem (
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class DebtActivity : ComponentActivity() {
    private lateinit var email: String
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                                }
                            )
                        }
                        composable("profile") {
                            val em = intent.getStringExtra("EMAIL")
                            if (em != null) {
                                ProfileScreen(
                                    onBackClick = { navController.popBackStack() },
                                    em = em
                                )
                            }
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

@Composable
fun AddDebtDialog(onDismiss: () -> Unit) {
    val debtName = remember { mutableStateOf("") }
    val paymentAmount = remember { mutableStateOf("") }
    val selectedDebtType = remember { mutableStateOf("") }
    val interestRate = remember { mutableStateOf("") }
    val paymentDate = remember { mutableStateOf("") }

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
                    listOf("Ипотека", "Кредит", "Долг").forEach { option ->
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
                    OutlinedTextField(
                        value = paymentDate.value,
                        onValueChange = { paymentDate.value = it },
                        label = { Text("Дата возврата") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        placeholder = { Text("YYYY-MM-DD") }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
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


@Composable
fun BoxList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = true
    ) {
        items(7) { _ ->
            LoanBox()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
@Composable
fun LoanBox() {
    var openDialog by remember { mutableStateOf(false) }
    var paymentAmount by remember { mutableStateOf("") }
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 96.dp.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to 1)
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .offset {
                IntOffset(swipeableState.offset.value.roundToInt(), 0)
            }
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .border(
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                shape = RoundedCornerShape(8.dp)
            ),
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
                Column {
                    Text("03.04.2024", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Название", style = MaterialTheme.typography.titleMedium)
                }
                Column {
                    Text("10000 ₽", style = MaterialTheme.typography.titleLarge)
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
                    progress = { 0.5f },
                    modifier = Modifier
                        .width(180.dp)
                        .height(20.dp)
                )
                Button(
                    onClick = { openDialog = true },
                    modifier = Modifier
                        .height(30.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Оплатить")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Ипотека", style = MaterialTheme.typography.titleMedium)
        }

        if (swipeableState.currentValue == 1) {
            AlertDialog(
                onDismissRequest = {
                    coroutineScope.launch {
                        swipeableState.snapTo(0)
                    }
                },
                title = { Text("Удалить этот элемент?") },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            swipeableState.snapTo(0)
                            // Handle delete logic here
                        }
                    }) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            swipeableState.snapTo(0)
                        }
                    }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }

    if (openDialog) {
        Dialog(onDismissRequest = { openDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Название долга", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = paymentAmount,
                        onValueChange = { paymentAmount = it },
                        label = { Text("Введите сумму") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { openDialog = false }) {
                            Text("Отмена")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            // Handle payment logic here
                            openDialog = false
                        }) {
                            Text("Оплатить")
                        }
                    }
                }
            }
        }
    }
}

