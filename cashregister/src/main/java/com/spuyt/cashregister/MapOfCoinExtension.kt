package com.spuyt.cashregister

fun Map<Coin, Long>.coinValue() = this.entries.map { it.value.times(it.key.minorValue) }.sum()

fun Map<Coin, Long>.add(other:Map<Coin, Long>){
    for(){

    }
}
