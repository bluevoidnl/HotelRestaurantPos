package com.spuyt.cashregister

fun Map<Coin, Long>.coinValue() = this.entries.map { it.value.times(it.key.minorValue) }.sum()

fun MutableMap<Coin, Long>.add(other: Map<Coin, Long>) {
    Coin.values().forEach { coin ->
        val add = other[coin]
        if (add != null) {
            val old = this[coin]
            if (old == null) {
                this[coin] = add
            } else {
                this[coin] = old + add
            }
        }
    }
}
