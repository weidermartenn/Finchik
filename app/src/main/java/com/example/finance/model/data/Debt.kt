package com.example.finance.model.data

import java.sql.Timestamp

data class Debt(
    val id: Int,
    val createdAt: Timestamp,
    val title: String?,
    val amount: Double,
    val debtType: Int,
    val interestRate: Double,
    val returnDate: Timestamp,
    val paid: Double,
    val isPaid: Boolean
)
