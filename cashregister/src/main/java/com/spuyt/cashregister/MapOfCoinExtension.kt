package com.spuyt.cashregister

fun Map<Coin, Long>.coinValue() = this.entries.map { it.value.times(it.key.minorValue) }.sum()

fun Map<Coin, Long>.numberOfCoins() = this.values.sum()

fun MutableMap<Coin, Long>.add(other: Map<Coin, Long>) {
    Coin.values().forEach { coin ->
        val add = other[coin]
        if (add != null) {
            val old = this[coin]
            if (old == null) {
                this[coin] = add
            } else {
                this[coin] = old.plus(add)
            }
        }
    }
}

fun MutableMap<Coin, Long>.minus(other: Map<Coin, Long>) {
    Coin.values().forEach { coin ->
        val minus = other[coin]
        if (minus != null) {
            val old = this[coin]
            if (old == null) {
                throw CashRegister.TransactionException("can not have negative coin")
            } else {
                val newVal = old.minus(minus)
                if (newVal < 0L) throw CashRegister.TransactionException("can not have negative coin")
                else {
                    this[coin] = newVal
                }
            }
        }
    }
}
