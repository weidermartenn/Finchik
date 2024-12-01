package com.example.finance.model.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String = "",
    val email: String? = null,
    val userId: String? = null
)
