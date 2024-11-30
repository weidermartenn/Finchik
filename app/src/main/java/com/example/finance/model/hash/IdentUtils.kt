package com.example.finance.model.hash

import kotlin.random.Random

object IdentUtils {
    private const val CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    fun generateId(): String {
        return (1..3)
            .map { Random.nextInt(0, CHAR_POOL.length) }
            .map(CHAR_POOL::get)
            .joinToString("")
    }
}
