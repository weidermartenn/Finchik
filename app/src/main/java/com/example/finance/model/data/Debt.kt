package com.example.finance.model.data

import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class Debt(
    val id: Long? = null,
    val title: String? = null,
    val amount: Double,
    val paid: Double,
    val isPaid: Boolean? = null,
    val debtType: Int,
    val interestRate: Double? = null,
    val returnDate: String,
    val createdAt: String? = null,
    val userId: String
)
