package com.example.finance.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.finance.R

@Composable
fun RegisterScreen(onRegisterComplete: (String, String, String) -> Unit, backToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Состояния ошибок для каждого поля
    var emailError by remember { mutableStateOf<String?>(null)}
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Функция валидации
    fun validateFields(context: Context): Boolean {
        var isValid = true
        emailError = if (email.isBlank()) {
            isValid = false
            context.getString(R.string.EMPTY_FIELD_EXCEPTION)
        } else null

        usernameError = if (username.isBlank()) {
            isValid = false
            context.getString(R.string.EMPTY_FIELD_EXCEPTION)
        } else null

        passwordError = if (password.isBlank()) {
            isValid = false
            context.getString(R.string.EMPTY_FIELD_EXCEPTION)
        } else null

        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.dm_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(220.dp)
                .padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = stringResource(id = R.string.REGISTRATION_TITLE),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null // Убираем ошибку при изменении
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            label = { Text(text = stringResource(id = R.string.email)) },
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
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
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = null // Убираем ошибку при изменении
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            label = { Text(text = stringResource(id = R.string.username)) },
            isError = usernameError != null,
            modifier = Modifier.fillMaxWidth()
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
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null // Убираем ошибку при изменении
            },
            label = { Text(text = stringResource(id = R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError != null) {
            Text(
                text = passwordError ?: "",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 10.dp)
            )
        }
        Spacer(modifier = Modifier.height(56.dp))
        Button(
            onClick = {
                if (validateFields(context)) {
                    onRegisterComplete(email, username, password)
                }
            },
            modifier = Modifier
                .width(240.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = stringResource(id = R.string.register_button),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.ALREADY_HAVE_AN_ACCOUNT),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = { backToLogin() },
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.login_button),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
