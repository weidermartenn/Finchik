package com.example.finance.model.data

import kotlinx.serialization.Serializable

@Serializable
data class DebtType(
    val id: Long,
    val name: String
)
