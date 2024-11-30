package com.example.finance.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.finance.R
import com.example.finance.model.supabase.SupabaseHelper


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBackClick: () -> Unit, em: String) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isEditable by remember { mutableStateOf(false) }
    // Состояния ошибок для каждого поля
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val supabaseHelper = remember { SupabaseHelper() }

    LaunchedEffect(em) {
        try {
            val user = supabaseHelper.fetchUserData(em)
            username = user.username
            email = user.email
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error fetching user data: ${e.localizedMessage}")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.BACK),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Localized description",
                modifier = Modifier
                    .size(180.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = R.string.PROFILE_INFO_TITLE),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = null
                },
                enabled = isEditable,
                textStyle = MaterialTheme.typography.bodyMedium,
                label = { Text(text = stringResource(id = R.string.username)) },
                isError = usernameError != null,
                modifier = Modifier.width(350.dp)
            )
            if (usernameError != null) {
                Text(
                    text = usernameError ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                enabled = isEditable,
                textStyle = MaterialTheme.typography.bodyMedium,
                label = { Text(text = stringResource(id = R.string.email)) },
                isError = emailError != null,
                modifier = Modifier.width(350.dp)
            )
            if (emailError != null) {
                Text(
                    text = emailError ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    isEditable = !isEditable
                },
                modifier = Modifier
                    .width(200.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.edit_button),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}