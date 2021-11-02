package uk.ac.napier.sa.controller

import org.jetbrains.annotations.Contract
import uk.ac.napier.sa.model.RemoteDatabaseManager

interface RemoteController {
    fun retrieveProduct(id: Int): String?
    fun changePrice(id: Int, newPrice: Double): String
    fun purchase(customer: Int, product: Int): String
    fun enrolOnLoyaltyCardScheme(id: Int): String?

    @Contract(pure = true)
    fun finance(choice: Char): String
    fun printLastNPurchases(n: Int)
    fun generateReport(): String?
    fun rdbm(): RemoteDatabaseManager?
    fun stockMonitor(): String?
    fun addSale(id: Int, selected: Int): String?
}