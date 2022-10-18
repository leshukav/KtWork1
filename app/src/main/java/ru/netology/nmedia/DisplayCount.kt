package ru.netology.nmedia

import java.math.BigDecimal

object DisplayCount {
    fun logic(count: Int): String {
        return when (count) {
            in 1000..9999 -> {
                ((count / 1000.0).toBigDecimal().setScale(1, BigDecimal.ROUND_DOWN)
                    .toString() + "K")
            }
            in 10_000..999_999 -> {
                ((count / 1000.0).toBigDecimal().setScale(0, BigDecimal.ROUND_DOWN)
                    .toString() + "K")
            }
            in 1_000_000..10_000_000 -> {
                (count / 1_000_000.0).toBigDecimal().setScale(0, BigDecimal.ROUND_DOWN)
                    .toString() + "M"
            }
            else -> {
                count.toString()
            }
        }
    }
}