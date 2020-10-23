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
     * @throws TransactionException If the transaction cannot be performed,
     * eg: Price is negative or zero, Too little coin paid or No exact change can be returned
     *
     * @return The change that was returned.
     */
    @Throws(TransactionException::class)
    @Synchronized
    fun performTransaction(price: Long, paid: Map<Coin, Long>): Map<Coin, Long> {
        if (price <= 0L) throw TransactionException("0 or negative value transactions are not allowed")
        val valuePaid = paid.coinValue()
        if (valuePaid < price) throw TransactionException("Paid too little $valuePaid $price")

        val changeValue = valuePaid - price
        return if (changeValue == 0L) {
            // it was an exact payment, add to cash and return 0 coins
            cashContent.addCoin(paid)
            mapOf()
        } else {
            // Too many coin can be given so some of the coins that were paid need to be given back,
            // add these to the set of possible coins to pay with.
            // Create defensive copy, to be able too roll back if transaction fails
            val availableCoins = cashContent.toMutableMap()
            availableCoins.addCoin(paid)

            val changeCoins =
                findChangeWithLeastCoins(changeValue, 0, availableCoins)
            if (changeCoins == null) {
                throw TransactionException("Can not pay exact change")
            } else {
                // changeCoins are available and the transaction can be execute
                cashContent.addCoin(paid)
                cashContent.minusCoin(changeCoins)
                changeCoins
            }
        }
    }

    /**
     * Recursive function to find change with the least coins possible.
     *
     *  @param changeValue The total value that the change should be.
     *  @param currentCoinOrdinal The ordinal of the Coin.values(): which coin to handle in this call.
     *  @param currentChangeBuilding The set of coins that were added already.
     *  @param availableCoins The total set of coins that can be used to generate the change.
     *
     *  @return The Map of coins representing the change, null if no solution is possible
     */
    private fun findChangeWithLeastCoins(
        changeValue: Long,
        currentCoinOrdinal: Int,
        availableCoins: Map<Coin, Long>,
        currentChangeBuilding: MutableMap<Coin, Long> = mutableMapOf()
    ): Map<Coin, Long>? {
        val currentCoin = Coin.values()[currentCoinOrdinal]
        val maxCoinsInCash = cashContent[currentCoin] ?: 0
        val currentValueInChange = currentChangeBuilding.coinValue()
        val maxCurrentCoinsNeeded = (changeValue-currentValueInChange) / currentCoin.minorValue
        val maxCoins = min(maxCoinsInCash, maxCurrentCoinsNeeded)

        // start with most coins that are available and fit the remaining change to get a solution with the least coins
        for (nrCoins: Long in maxCoins downTo 0) {
            val newChangeBuilding = currentChangeBuilding.toMutableMap()
            if (nrCoins > 0) {
                newChangeBuilding.addCoin(mapOf(currentCoin to nrCoins))
            }
            val newValue = newChangeBuilding.coinValue()
            when {
                newValue < changeValue -> {
                    // ok new fork, try add next coin
                    val newCoinOrdinal = currentCoinOrdinal + 1
                    if (newCoinOrdinal < Coin.values().size) {
                        return findChangeWithLeastCoins(
                            changeValue,
                            newCoinOrdinal,
                            availableCoins,
                            newChangeBuilding
                        )
                    } else {
                        // no coins can be added, give up this branche
                    }
                }
                // we have a solution
                newValue == changeValue -> return newChangeBuilding
                newValue > changeValue -> {
                    // dead end, do nothing
                }
            }
        }
        return null
    }

    /**
     * Represents an error during a transaction.
     */
    class TransactionException(
        message: String, cause: Throwable? = null
    ) :
        Exception(message, cause)
}