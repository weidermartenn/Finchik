package com.example.finance.model

import java.sql.Timestamp

data class User(
    val id: Int,
    val createdAt: Timestamp,
    val username: String,
    val email: String?,
    val password: String
)
