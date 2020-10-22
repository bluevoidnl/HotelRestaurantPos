package com.spuyt

import org.junit.Test
import com.spuyt.cashregister.Coin
import com.spuyt.cashregister.coinValue
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
}