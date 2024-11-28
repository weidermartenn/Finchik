package com.example.finance.model.supabase

import android.net.http.HttpException
import android.util.Log
import com.example.finance.model.data.Debt
import com.example.finance.model.data.User
import com.example.finance.model.data.UserDebts
import com.example.finance.model.hash.HashUtils
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.*
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(SupabaseInternal::class)
class SupabaseHelper {
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
            val user = supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = hashedPassword
            }
            Log.d("SupabaseHelper", "Пользователь успешно вошел")
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Ошибка при авторизации: ${e.localizedMessage}")
            throw e // Пробрасываем исключение дальше
        }
    }

    suspend fun signUpWithEmail(email: String, username: String, password: String) {
        val hashedPassword = HashUtils.sha256(password)
        try {
            val user = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = hashedPassword
                data = buildJsonObject {
                    put("username", username)
                }
            }
            Log.d("SupabaseHelper", "Пользователь успешно добавлен")

            addUserToDatabase(email, username)
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Ошибка при регистрации: ${e.localizedMessage}")
            throw e // Пробрасываем исключение дальше
        }
    }

    private suspend fun addUserToDatabase(email: String, username: String) {
        try {
            supabaseClient.postgrest["users"].insert(
                User(
                    username = username,
                    email = email
                )
            )
            Log.d("SupabaseHelper", "Пользователь добавлен в таблицу users")
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Ошибка при добавлении пользователя в таблицу users: ${e.localizedMessage}")
            throw e // Пробрасываем исключение дальше
        }
    }

    suspend fun fetchUserData(userId: Int): Pair<User, List<Debt>> {
        try {
            // Получение данных пользователя
            val user = supabaseClient
                .from("cities")
                .select(columns = Columns.list("id, username, email"))
                .decodeSingle<User>()
                .equals(userId)

            // Получение связей пользователя с долгами из user_debts
            val userDebts = supabaseClient
                .from("user_debts")
                .select(columns = Columns.list("debtId"))
                .decodeList<UserDebts>()
                .equals(userId)

            // Список ID долгов, связанных с пользователем
            val debtIds = userDebts.map { it.debtId }

            // Получение данных о долгах
            val debts = supabaseClient.postgrest["debts"].select {
                in_("id", debtIds)
            }.decodeList<Debt>()

            return Pair(user, debts)
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Ошибка при получении данных: ${e.localizedMessage}")
            throw e // Пробрасываем исключение дальше
        }
    }
}
