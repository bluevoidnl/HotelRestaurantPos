package com.spuyt.cashregister

import java.lang.Long.min

/**
 * A cash register.
 *
 * @property cashContent The current change in the cash register.
 */
class CashRegister(private val cashContent: MutableMap<Coin, Long> = mutableMapOf()) {

    fun getCashRegisterValue() = cashContent.coinValue()

    /**
     * Performs a transaction.
     *
     * @param price The price of the goods.
     * @param paid The amount paid by the customer.
     * @throws TransactionException If the transaction cannot be performed.
     *
     * @return The change that was returned.
     */
    @Throws(TransactionException::class)
    @Synchronized
    fun performTransaction(price: Long, paid: Map<Coin, Long>): Map<Coin, Long> {
        if (price <= 0L) throw TransactionException("0 or negative value transactions are not allowed")
        val valuePaid = paid.coinValue()
        if (valuePaid < price) throw TransactionException("paid too little $valuePaid $price")

        val changeValue = valuePaid - price
        return if (changeValue == 0L) {
            // it was an exact payment, add to cash and return 0 coins
            cashContent.addCoin(paid)
            mapOf()
        } else {
            // create defensive copy, to be able too roll back if transaction fails
            val cashContentCopy = cashContent.toMutableMap()
            cashContentCopy.addCoin(paid)

            val changeOptions = mutableListOf<Map<Coin, Long>>()
            generateChangeOptions(changeValue, changeOptions, 0, mutableMapOf(), cashContentCopy)
            if (changeOptions.isEmpty()) {
                throw TransactionException("can not pay exact change")
            } else {
                val changeCoins = changeOptions[0]
                // we were able to create changeCoins and can execute the transaction
                // replace the current cashContent with the new one
                cashContentCopy.minusCoin(changeCoins)
                cashContent.clear()
                cashContent.addCoin(cashContentCopy)
                changeCoins
            }
        }
    }

    private fun generateChangeOptions(
        change: Long,
        changeOptions: MutableList<Map<Coin, Long>>,
        currentCoinOrdinal: Int,
        currentChangeBuilding: MutableMap<Coin, Long>,
        cashContent: Map<Coin, Long>
    ) {
        val currentCoin = Coin.values()[currentCoinOrdinal]
        val maxCoinsInCash = cashContent[currentCoin] ?: 0
        val maxCurrentCoin = change / currentCoin.minorValue
        val maxCoins = min(maxCoinsInCash, maxCurrentCoin)

        // start with most coins that fit the remaining change
        for (nrCoins: Long in maxCoins downTo 0) {
            val newChangeBuilding = currentChangeBuilding.toMutableMap()
            if (nrCoins > 0) {
                newChangeBuilding.addCoin(mapOf(currentCoin to nrCoins))
            }
            val newValue = newChangeBuilding.coinValue()
            when {
                newValue < change -> {
                    // ok new fork, try add next coin
                    val newCoinOrdinal = currentCoinOrdinal + 1
                    if (newCoinOrdinal < Coin.values().size) {
                        generateChangeOptions(
                            change,
                            changeOptions,
                            newCoinOrdinal,
                            newChangeBuilding,
                            cashContent
                        )
                    } else {
                        // we can not add more coins, give up this branche
                    }
                }
                newValue == change -> {
                    // we have a solution
                    changeOptions.add(newChangeBuilding)
                    return
                }
                newValue > change -> {
                    // dead end, do nothing
                }
            }
            if (changeOptions.isNotEmpty()) {
                return
            }
        }
    }

    /**
     * Represents an error during a transaction.
     */
    class TransactionException(
        message: String, cause: Throwable? = null
    ) :
        Exception(message, cause)
}
