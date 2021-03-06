package com.spuyt

import com.spuyt.cashregister.CashRegister
import com.spuyt.cashregister.Coin
import com.spuyt.cashregister.coinValue
import com.spuyt.cashregister.numberOfCoins
import org.junit.Assert
import org.junit.Assert.assertThrows
import org.junit.Test

class CashRegisterTest {
    companion object {
        const val VALUE_OF_ONE_COIN_OF_EACH = 388L
    }

    private fun getMapOfAllCoins(nr: Long) = mutableMapOf(
        Coin.TWO_EURO to nr,
        Coin.ONE_EURO to nr,
        Coin.FIFTY_CENT to nr,
        Coin.TWENTY_CENT to nr,
        Coin.TEN_CENT to nr,
        Coin.FIVE_CENT to nr,
        Coin.TWO_CENT to nr,
        Coin.ONE_CENT to nr
    )

    @Test
    fun test_valueOfOneCoinOfEach_is388() {
        val mapOfCoins = getMapOfAllCoins(1)
        Assert.assertEquals(VALUE_OF_ONE_COIN_OF_EACH, mapOfCoins.coinValue())
    }

    /**
     * Test error thrown when the paid coins are too little
     */
    @Test
    fun test_payTooLittle_throwsException() {
        val cashRegister = CashRegister()
        val expectedEndCashRegisterValue = cashRegister.getCashRegisterValue()
        val paid = mapOf(Coin.ONE_EURO to 1L)
        assertThrows(CashRegister.TransactionException::class.java) {
            cashRegister.performTransaction(200, paid)
        }
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }

    /**
     * Test error thrown when the paid coins are too little
     */
    @Test
    fun test_0priceTransaction_throwsException() {
        val cashRegister = CashRegister()
        val expectedEndCashRegisterValue = cashRegister.getCashRegisterValue()
        val paid = mapOf(Coin.ONE_EURO to 1L)
        assertThrows(CashRegister.TransactionException::class.java) {
            cashRegister.performTransaction(0, paid)
        }
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }

    /**
     * Test error thrown when the cashRegister has no exact change to give back
     */
    @Test
    fun test_TooLittleChange_throwsException() {
        val coinsInCash = getMapOfAllCoins(20)
        coinsInCash.remove(Coin.ONE_CENT)
        val cashRegister = CashRegister(coinsInCash)
        val expectedEndCashRegisterValue = cashRegister.getCashRegisterValue()
        val paid = mapOf(Coin.TWO_EURO to 2L, Coin.ONE_EURO to 1L, Coin.FIFTY_CENT to 1L)
        assertThrows(CashRegister.TransactionException::class.java) {
            cashRegister.performTransaction(549, paid)
        }
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }

    /**
     * Test exact payment with 1 coin
     */
    @Test
    fun test_payWithOneCoin_noChange() {
        val cashRegister = CashRegister()
        val paid = mapOf(Coin.ONE_EURO to 1L)
        val expectedEndCashRegisterValue = cashRegister.getCashRegisterValue().plus(100)
        val change = cashRegister.performTransaction(100, paid)
        Assert.assertTrue(change.isEmpty())
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }

    /**
     * Test exact payment with 1 coin from each
     */
    @Test
    fun test_payWithOneOfEach_noChange() {
        val cashRegister = CashRegister()
        val paid = getMapOfAllCoins(1L)
        val expectedEndCashRegisterValue =
            cashRegister.getCashRegisterValue().plus(VALUE_OF_ONE_COIN_OF_EACH)
        val change = cashRegister.performTransaction(VALUE_OF_ONE_COIN_OF_EACH, paid)
        Assert.assertTrue(change.isEmpty())
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }

    /**
     * Test getting 1 coin back
     */
    @Test
    fun test_payWithOnCoin_changeIsOneCoin() {
        val cashRegister = CashRegister(getMapOfAllCoins(20))
        val paid = mapOf(Coin.ONE_EURO to 1L)
        val expectedEndCashRegisterValue = cashRegister.getCashRegisterValue().plus(80)
        val change = cashRegister.performTransaction(80, paid)
        Assert.assertEquals(1, change.numberOfCoins())
        Assert.assertEquals(20, change.coinValue())
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }

    /**
     * Test getting 2 of the same coin back
     */
    @Test
    fun test_payWithOne_ChangeIsTwoSameCoins() {
        val cashRegister = CashRegister(getMapOfAllCoins(20))
        val paid = mapOf(Coin.ONE_EURO to 1L)
        val expectedEndCashRegisterValue = cashRegister.getCashRegisterValue().plus(60)
        val change = cashRegister.performTransaction(60, paid)
        Assert.assertEquals(40, change.coinValue())
        Assert.assertEquals(2, change.numberOfCoins())
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }

    /**
     * Test paying 1 to 1000 cents, get correct change back
     */
    @Test
    fun test_payPriceOf1To1000cents_ChangeIsCorrect() {
        for (price in 1L..1000L) {
            val cashRegister = CashRegister(getMapOfAllCoins(20))
            val paid = getMapOfAllCoins(3)
            val expectedEndCashRegisterValue = cashRegister.getCashRegisterValue().plus(price)
            val change = cashRegister.performTransaction(price, paid)
            Assert.assertEquals(paid.coinValue() - price, change.coinValue())
            Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
        }
    }

    /**
     * Test getting 6 of the same coin back, when there are some coins missing in the cashRegister
     */
    @Test
    fun test_payWhenCashRegisterHasLimitedCoins_ChangeIs6Times20CentsCoin() {
        val cashContent = getMapOfAllCoins(20)
        cashContent.remove(Coin.ONE_EURO)
        cashContent.remove(Coin.FIFTY_CENT)
        val cashRegister = CashRegister(cashContent)

        val paid = mapOf(Coin.TWO_EURO to 2L)
        val expectedEndCashRegisterValue = cashRegister.getCashRegisterValue().plus(280)
        val change = cashRegister.performTransaction(280, paid)
        Assert.assertEquals(mapOf(Coin.TWENTY_CENT to 6L), change)
        Assert.assertEquals(120, change.coinValue())
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }

    /**
     * Test getting some of your own coins back (the 2 euro)
     */
    @Test
    fun test_payTooMuch_changeContainsOneOfTooMuchPaid() {
        val cashRegister = CashRegister(getMapOfAllCoins(20))
        val paid = mapOf(Coin.TWO_EURO to 2L)
        val expectedEndCashRegisterValue = cashRegister.getCashRegisterValue().plus(12)
        val change = cashRegister.performTransaction(12, paid)
        // change should be 388, 1 from each coin
        Assert.assertEquals(VALUE_OF_ONE_COIN_OF_EACH, change.coinValue())
        Assert.assertEquals(8, change.numberOfCoins())
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }

    /**
     * Test getting all extra coins back that you give too much
     * Pay 388 with 2 coins from each, expect 1 coin from each back
     */
    @Test
    fun test_payWithTwoOfEach_changeIsOneOfEach() {
        val cashRegister = CashRegister(getMapOfAllCoins(0))
        val paid = getMapOfAllCoins(2L)
        val expectedEndCashRegisterValue =
            cashRegister.getCashRegisterValue().plus(VALUE_OF_ONE_COIN_OF_EACH)
        val change = cashRegister.performTransaction(VALUE_OF_ONE_COIN_OF_EACH, paid)
        // change should be 388, 1 from each coin
        Assert.assertEquals(VALUE_OF_ONE_COIN_OF_EACH, change.coinValue())
        Assert.assertEquals(8, change.numberOfCoins())
        Assert.assertEquals(expectedEndCashRegisterValue, cashRegister.getCashRegisterValue())
    }
}