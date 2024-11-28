package com.example.finance.model.data

import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class Debt(
    val id: Int? = null,
    val title: String?,
    val amount: Double,
    val paid: Double,
    val isPaid: Boolean,
    val debtType: Int,
    val interestRate: Double,
    val returnDate: String,
    val createdAt: String,
)
