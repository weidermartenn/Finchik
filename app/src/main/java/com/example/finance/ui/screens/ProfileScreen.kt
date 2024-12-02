import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.finance.R
import com.example.finance.model.supabase.SupabaseHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBackClick: () -> Unit, sharedPreferences: SharedPreferences) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var isEditable by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val originalUsername = remember { mutableStateOf("") }
    val originalEmail = remember { mutableStateOf("") }

    val supabaseHelper = remember { SupabaseHelper(sharedPreferences) }

    LaunchedEffect(Unit) {
        try {
            id = sharedPreferences.getString("userId", "") ?: ""
            if (id.isNotEmpty()) {
                val user = supabaseHelper.fetchUserData(id)
                username = user.username
                email = user.email.toString()
                originalUsername.value = username // Store original values
                originalEmail.value = email
            } else {
                Log.e("ProfileScreen", "User ID not found in SharedPreferences")
            }
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error fetching user data: ${e.localizedMessage}")
        }
    }

    val coroutineScope = rememberCoroutineScope()

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
                    // Revert to original values on cancel
                    if (isEditable) {
                        username = originalUsername.value
                        email = originalEmail.value
                    }
                    isEditable = !isEditable
                },
                modifier = Modifier.width(200.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = if (isEditable) stringResource(id = R.string.cancel_button) else stringResource(
                        id = R.string.edit_button
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (isEditable) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                supabaseHelper.updateUserInfo(id, email, username)
                                isEditable = false
                            } catch (e: Exception) {
                                Log.e("ProfileScreen", "Error updating user info: ${e.localizedMessage}")
                            }
                        }
                    },
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.save_button),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "ID: $id",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.LightGray
            )
        }
    }
}
