package com.example.finance.model.supabase

import android.util.Log
import com.example.finance.model.data.User
import com.example.finance.model.hash.HashUtils
import com.example.finance.model.hash.IdentUtils
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.*
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.plugins.websocket.WebSockets
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
            val authResult = supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = hashedPassword
            }
            Log.d("SupabaseHelper", "User signed in")
            fetchUserData(email)
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
                data = buildJsonObject {
                    put("username", username)
                }
            }
            Log.d("SupabaseHelper", "User added")
            addUserToDatabase(email, username)
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Ошибка при регистрации: ${e.localizedMessage}")
            throw e // Пробрасываем исключение дальше
        }
    }

    private suspend fun addUserToDatabase(email: String, username: String) {
        try {
            val userId = IdentUtils.generateId()
            supabaseClient.postgrest["users"].insert(
                User(
                    id = userId,
                    username = username,
                    email = email
                )
            )
            Log.d("SupabaseHelper", "User added to table users\nid = $userId")
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Ошибка при добавлении пользователя в таблицу users: ${e.localizedMessage}")
            throw e // Пробрасываем исключение дальше
        }
    }

    suspend fun fetchUserData(email: String?): User {
        try {
            val user = supabaseClient
                .from("users")
                .select(columns = Columns.list("id", "email", "username")) {
                    filter {
                        if (email != null) {
                            eq("email", email)
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
}
