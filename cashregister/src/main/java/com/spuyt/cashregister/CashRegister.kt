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
                findChangeWithLeastCoins(changeValue, availableCoins)
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
     * Function to find change with the least coins possible. It tries to add the maximum nr of coins of each type.
     *
     *  @param changeValue The total value that the change should be.
     *  @param availableCoins The total set of coins that can be used to generate the change.
     *
     *  @return The Map of coins representing the change, null if no solution is possible
     */
    private fun findChangeWithLeastCoins(
        changeValue: Long,
        availableCoins: Map<Coin, Long>
    ): Map<Coin, Long>? {
        val currentChangeBuilding: MutableMap<Coin, Long> = mutableMapOf()
        Coin.values().forEach { currentCoin ->
            val maxCoinsInCash = cashContent[currentCoin] ?: 0
            val remainingChangeToAdd = changeValue - currentChangeBuilding.coinValue()
            val maxCurrentCoinsNeeded = remainingChangeToAdd / currentCoin.minorValue
            val maxCoins = min(maxCoinsInCash, maxCurrentCoinsNeeded)
            if (maxCoins > 0) {
                currentChangeBuilding.addCoin(mapOf(currentCoin to maxCoins))
            }
            if (currentChangeBuilding.coinValue() == changeValue) {
                return currentChangeBuilding
            }
        }
        // all coins were tested and non remain: no solution possible
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