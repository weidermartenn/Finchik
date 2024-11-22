package com.example.finance

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.FinanceTheme
import com.example.finance.ui.screens.AddDebtScreen
import com.example.finance.ui.screens.DebtScreen
import com.example.finance.ui.screens.ProfileScreen
import com.example.finance.ui.theme.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

data class TabBarItem (
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class DebtActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
                            ProfileScreen(
                                onBackClick = { navController.popBackStack() }
                            )
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

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun LoanBox() {
    var openDialog = remember { mutableStateOf(false) }
    val paymentAmount = remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
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
            val showPaidAmount = remember { mutableStateOf(false) }
            var offset = remember { mutableStateOf(Offset.Zero) }

            Text("Название", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(15.dp))
            Text("Дата займа: 03.04.2024", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Сумма займа: 10000", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Выплачено: 5000", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(25.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            offset.value = tapOffset
                            showPaidAmount.value = true
                        }
                    }
            ) {
                LinearProgressIndicator(
                    progress = { 0.5f },
                    modifier = Modifier.fillMaxSize(),
                )
                Text(
                    text = "10000",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
                if (showPaidAmount.value) {
                    Box(
                        modifier = Modifier
                            .absoluteOffset(
                                x = with(LocalDensity.current) { offset.value.x.toDp() - 40.dp },
                                y = with(LocalDensity.current) { offset.value.y.toDp() - 50.dp }
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Выплачено: 5000",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    LaunchedEffect(Unit) {
                        delay(2000)
                        showPaidAmount.value = false
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
            Button(
                onClick = { openDialog.value = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Оплатить")
            }
        }
    }

    if (openDialog.value) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
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
                        value = paymentAmount.value,
                        onValueChange = { paymentAmount.value = it },
                        label = { Text("Введите сумму") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { openDialog.value = false }) {
                            Text("Отмена")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            // Handle payment logic here
                            openDialog.value = false
                        }) {
                            Text("Оплатить")
                        }
                    }
                }
            }
        }
    }
}