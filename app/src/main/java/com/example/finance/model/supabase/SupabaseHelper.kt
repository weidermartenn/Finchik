package com.example.finance.model.supabase

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.finance.model.data.Debt
import com.example.finance.model.data.DebtType
import com.example.finance.model.data.User
import com.example.finance.model.hash.HashUtils
import com.example.finance.model.hash.IdentUtils
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.*
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

@OptIn(SupabaseInternal::class)
class SupabaseHelper(private val sharedPreferences: SharedPreferences) {
    private val supabaseClient = createSupabaseClient(
        supabaseUrl = "https://ndnxrwjkcvcpxwnnweit.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5kbnhyd2prY3ZjcHh3bm53ZWl0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzIwMzA4NzgsImV4cCI6MjA0NzYwNjg3OH0.0sN6YAiSSLA1NAmXuX752CPhTrJzZfPgnhJEp5OUWB8"
    ) {
        install(Postgrest)
        install(Auth)
        install(Realtime)
        httpConfig {
            this.install(WebSockets)
        }
    }

    suspend fun signInWithEmail(email: String, password: String) {
        val hashedPassword = HashUtils.sha256(password)
        try {
            val authResult = supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = hashedPassword
            }
            Log.d("SupabaseHelper", "User signed in")
            fetchalldebts()
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Auth exception: ${e.localizedMessage}")
            throw e
        }
    }

    suspend fun signUpWithEmail(email: String, username: String, password: String) {
        val hashedPassword = HashUtils.sha256(password)
        try {
            val user = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = hashedPassword
            }
            Log.d("SupabaseHelper", "User added")
            addUserToDatabase(email, username, sharedPreferences)
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Ошибка при регистрации: ${e.localizedMessage}")
            throw e // Пробрасываем исключение дальше
        }
    }

    private suspend fun addUserToDatabase(email: String, username: String, sharedPreferences: SharedPreferences) {
        try {
            val userId = IdentUtils.generateId()
            supabaseClient.postgrest["users"].insert(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "userId" to userId
                )
            )
            sharedPreferences.edit().putString("userId", userId).apply()
            Log.d("SupabaseHelper", "User added to table users\nid = $userId")
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Ошибка при добавлении пользователя в таблицу users: ${e.localizedMessage}")
            throw e // Пробрасываем исключение дальше
        }
    }

    suspend fun addDebtToDatabase(
        debtName: String,
        paymentAmount: String,
        debtType: String,
        interestRate: String?,
        paymentDate: String,
        userId: String
    ) {
        try {
            val debtTypeId = when (debtType) {
                "Кредит" -> 1
                "Ипотека" -> 2
                "Долг" -> 3
                else -> {}
            }

            val debtId = UUID.randomUUID()

            // Добавление долга в таблицу debts
            supabaseClient.postgrest["debts"].insert(
                mapOf(
                    "id" to debtId,
                    "title" to debtName,
                    "amount" to paymentAmount.toBigDecimal(),
                    "debtType" to debtTypeId,
                    "interestRate" to interestRate?.toBigDecimalOrNull(),
                    "returnDate" to paymentDate,
                    "paid" to 0.toBigDecimal(),
                    "isPaid" to false,
                    "createdAt" to System.currentTimeMillis()
                )
            )

            // Связываем долг с пользователем в таблице users_debt
            supabaseClient.postgrest["users_debt"].insert(
                mapOf(
                    "userId" to userId,
                    "debtId" to debtId
                )
            )

            Log.d("SupabaseHelper", "Debt successfully added to database")
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Error adding debt to database: ${e.localizedMessage}")
            throw e
        }
    }

    suspend fun fetchalldebts() {
        try {
            val debts = supabaseClient
                .from("debts")
                .select()
                .decodeList<Debt>()

            Log.d("SupabaseHelper", "DEBTS: $debts")
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Failed to fetch user data: ${e.localizedMessage}")
            throw e
        }
    }


    suspend fun fetchUserData(id: String?): User {
        try {
            val user = supabaseClient
                .from("users")
                .select(columns = Columns.list("id", "email", "username", "userId")) {
                    filter {
                        if (id != null) {
                            eq("userId", id)
                        }
                    }
                }
                .decodeSingle<User>()
            Log.d("SupabaseHelper", "User data fetched: $user")
            return user
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Failed to fetch user data: ${e.localizedMessage}")
            throw e
        }
    }



    suspend fun updateUserInfo(id: String, newEmail: String, newUsername: String) {
        try {
            supabaseClient.auth.updateUser(updateCurrentUser = true) {
                email = newEmail
            }

            supabaseClient
                .postgrest["users"]
                .update(
                    {
                        set("username", newUsername)
                        set("email", newEmail)
                    }
                ) {
                    filter {
                        eq("userId", id)
                    }
                }

            Log.d("SupabaseClient", "User data updated")
            fetchUserData(newEmail)
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Failed to update data: ${e.localizedMessage}")
            throw e
        }
    }

}
