package uk.ac.napier.sa.controller

import org.jetbrains.annotations.Contract

interface RemoteController {
    fun retrieveProduct(id: Int): String?

    fun changePrice(id: Int, newPrice: Double): String

    fun addSale(id: Int, sale: Int): String

    fun purchase(customer: Int, product: Int): String

    fun enrolOnLoyaltyCardScheme(id: Int): String?

    @Contract(pure = true)
    fun finance(choice: Int): String

    fun printLastNPurchases(n: Int)

    fun generateReport(): String

    fun stockMonitor(): String
}