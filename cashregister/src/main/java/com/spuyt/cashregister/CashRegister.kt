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

    fun performTransaction(price: Long, paid: Map<Coin, Long>): Map<Coin, Long> {
        if (price <= 0L) throw TransactionException("0 or negative value transactions are not allowed")
        val valuePaid = paid.coinValue()
        if (valuePaid < price) throw TransactionException("paid too little $valuePaid $price")

        val change = valuePaid - price
         if (change == 0L) {

            mapOf()
        } else {
            createChange(change, paid)
        }
    }



    private fun createChange(change: Long, paid: Map<Coin, Long>): Map<Coin, Long> {
        cashContent.copy()
    }

    /**
     * Represents an error during a transaction.
     */
    class TransactionException(message: String, cause: Throwable? = null) :
        Exception(message, cause)
}
