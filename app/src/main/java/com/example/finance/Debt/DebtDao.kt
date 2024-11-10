package com.example.finance.Debt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DebtDao {
    @Insert
    suspend fun insertDebt(debt: Debt): Long

    @Query("SELECT * FROM debts WHERE userId = :userId")
    suspend fun getDebtByUserId(userId: Long): List<Debt>

    @Query("SELECT * FROM debts WHERE debtId = :debtId")
    suspend fun getDebtById(debtId: Long): Debt?
}