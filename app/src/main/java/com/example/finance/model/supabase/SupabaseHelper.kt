package com.example.finance.model.supabase

import android.net.http.HttpException
import android.util.Log
import com.example.finance.model.data.User
import com.example.finance.model.hash.HashUtils
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
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
        install(Realtime)
        httpConfig {
            this.install(WebSockets)
        }
    }

    suspend fun signInWithEmail(email: String, password: String) {
        return try {
            val user = supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = HashUtils.sha256(password)
            }
        } catch (e: Exception) {

        }
    }

    suspend fun signUpWithEmail(email: String, username: String, password: String) {
        return try {
            val user = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = HashUtils.sha256(password)
                data = buildJsonObject {
                    put("username", username)
                }
            }
        } catch (e: Exception) {

        }
    }
}