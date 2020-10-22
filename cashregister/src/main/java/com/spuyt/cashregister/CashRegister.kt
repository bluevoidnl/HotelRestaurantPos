package com.spuyt.cashregister

/**
 * A cash register.
 *
 * @property change The current change in the cash register.
 */
class CashRegister(private val cashContent: MutableMap<Coin, Long> = mutableMapOf()) {
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
            cashContent.add(paid)
            mapOf()
        } else {
            // create defensive copy, to be able too roll back if transaction fails
            val cashContentCopy = cashContent.toMutableMap()
            cashContentCopy.add(paid)
            val changeCoins = createChange(changeValue, cashContentCopy)
            // we were able to create changeCoins and can execute the transaction
            // replace the current cashContent with the new one
            cashContent.clear()
            cashContent.add(cashContentCopy)
            changeCoins
        }
    }

    private fun createChange(
        change: Long,
        cashContentCopy: MutableMap<Coin, Long>
    ): Map<Coin, Long> {
        val changeOptions = generateChangeOptions(change)
        throw TransactionException("not implemented yet")
    }


    private fun generateChangeOptions(change: Long):Map<Long, Map<Coin, Long>>{
        val changeOptions = mutableMapOf<Long, Map<Coin, Long>>()


        return changeOptions
    }

    /**
     * Represents an error during a transaction.
     */
    class TransactionException(message: String, cause: Throwable? = null) :
        Exception(message, cause)
}
