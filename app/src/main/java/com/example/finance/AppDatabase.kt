package com.example.finance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.finance.Debt.Debt
import com.example.finance.Debt.DebtDao
import com.example.finance.User.User
import com.example.finance.User.UserDao

@Database(entities = [User::class, Debt::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun debtDao(): DebtDao
}