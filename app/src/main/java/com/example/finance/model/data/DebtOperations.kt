package com.example.finance.model.data

import kotlinx.serialization.Serializable

@Serializable
data class DebtOperations (
    val id: String,
    val createdAt: String,
    val amount: Double,
    val debtId: String
)