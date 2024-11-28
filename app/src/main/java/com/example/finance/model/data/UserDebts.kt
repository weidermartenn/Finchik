package com.example.finance.model.data

import kotlinx.serialization.Serializable

@Serializable
data class UserDebts(
    val id: String,
    val userId: Int,
    val debtId: Int
)
