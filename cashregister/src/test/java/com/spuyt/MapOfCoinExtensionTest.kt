package com.spuyt

import com.spuyt.cashregister.*
import org.junit.Test
import org.junit.Assert

class MapOfCoinExtensionTest {
    @Test
    fun test_EmptyMap_0value() {
        Assert.assertEquals(0, mapOf<Coin, Long>().coinValue())
    }

    @Test
    fun test_CoinsMap_CorrectValue() {
        val paid = mapOf(Coin.TWO_EURO to 1L, Coin.ONE_EURO to 2L)
        Assert.assertEquals(400, paid.coinValue())
    }

    @Test
    fun test_CoinsMaps_addCorrect() {
        val register = mutableMapOf(Coin.TWO_EURO to 1L, Coin.ONE_EURO to 1L)
        val paid = mapOf(Coin.ONE_EURO to 1L, Coin.FIFTY_CENT to 1L)
        register.addCoin(paid)
        Assert.assertEquals(450, register.coinValue())
    }

    @Test
    fun test_CoinsMaps_minusCorrect() {
        val register = mutableMapOf(Coin.TWO_EURO to 1L, Coin.ONE_EURO to 2L)
        val change = mapOf(Coin.ONE_EURO to 1L)
        register.minusCoin(change)
        Assert.assertEquals(300, register.coinValue())
    }

    @Test
    fun test_CoinsMaps_minusTrowsExceptionWhenTooLittleCoins() {
        val register = mutableMapOf(Coin.TWO_EURO to 1L, Coin.ONE_EURO to 2L)
        val change = mapOf(Coin.ONE_EURO to 3L)
        Assert.assertThrows(CashRegister.TransactionException::class.java) {
            register.minusCoin(change)
        }
    }
}