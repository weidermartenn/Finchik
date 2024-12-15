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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
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
            val userId = fetchUserId(email)
            sharedPreferences.edit()
                .putString("userId", userId)
                .putBoolean("isLogin", true)
                .apply()
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Auth exception: ${e.localizedMessage}")
            throw e
        }
    }

    private suspend fun fetchUserId(email: String) : String {
        try {
            val userId = supabaseClient
                .from("users")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("email", email)
                    }
                }
                .decodeSingle<User>()
            return userId.userId!!
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "User id fetch exception: ${e.localizedMessage}")
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
        id: String,
        title: String,
        amount: Double,
        debtType: String,
        interestRate: Double?,
        returnDate: String
    ) {
        try {
            val debtId = when (debtType) {
                "Ипотека" -> 1
                "Кредит" -> 2
                "Долг" -> 3
                else -> 0
            }

            val debt = Debt(
                id = IdentUtils.generateId(),
                title = title,
                amount = amount,
                paid = 0.0,
                isPaid = false,
                debtType = debtId,
                interestRate = interestRate,
                returnDate = returnDate,
                createdAt = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                userId = id
            )

            supabaseClient.postgrest["debts"].insert(debt)
            Log.d("SupabaseHelper", "Debt successfully added to database")
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Error adding debt to database: ${e.localizedMessage}")
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

    suspend fun fetchUserDebtsData(id: String?): List<Debt> {
        try {
            val debts = supabaseClient
                .from("debts")
                .select(columns = Columns.ALL) {
                    filter {
                        if (id != null) {
                            eq("userId", id)
                        }
                    }
                }
                .decodeList<Debt>()
            return debts
            Log.d("SupabaseHelper", "Fetched users debts: $debts")
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Failed to fetch user data: ${e.localizedMessage}")
            throw e
        }
    }


    suspend fun updateUserInfo(id: String, newEmail: String, newUsername: String) {
        try {
            val currentEmail = supabaseClient
                .postgrest["users"]
                .select() {
                    filter {
                        eq("userId", id)
                    }
                }
                .decodeSingle<User>().email

            if (currentEmail != newEmail) {
                supabaseClient.auth.updateUser(updateCurrentUser = true) {
                    email = newEmail
                }
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

    suspend fun updateDebtInfo(
        id: String,
        title: String,
        amount: Double,
        returnDate: String
    ) {
        try {
            supabaseClient
                .postgrest["debts"]
                .update(
                    {
                        set("title", title)
                        set("amount", amount)
                        set("returnDate", returnDate)
                    }
                ) {
                    filter {
                        eq("id", id)
                    }
                }
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Failed to update debt data: ${e.localizedMessage}")
            throw e
        }
    }

    suspend fun updatePaidData(
        id: String,
        deposit: Double
    ) {
        try {
            val debt = supabaseClient
                .postgrest["debts"]
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<Debt>()

            val paid = debt.paid
            val paymentAmount = debt.amount

            if (paid + deposit > paymentAmount) {
                throw IllegalArgumentException("Сумма оплаты превышает общую сумму долга.")
            } else {
                supabaseClient
                    .postgrest["debts"]
                    .update(
                        {
                            set("paid", (paid + deposit))
                        }
                    ) {
                        filter {
                            eq("id", id)
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Failed to update debt data: ${e.localizedMessage}")
            throw e
        }
    }

    suspend fun deleteDebt(debtId: String) {
        supabaseClient.from("debts").delete {
            filter {
                eq("id", debtId)
            }
        }
    }
}
