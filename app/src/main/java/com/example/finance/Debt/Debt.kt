package com.example.finance.Debt

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.finance.User.User

@Entity(
    tableName = "debts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Debt (
    @PrimaryKey(autoGenerate = true) val debtId: Long = 0,
    val amount: Double,
    val description: String,
    val userId: Long
)