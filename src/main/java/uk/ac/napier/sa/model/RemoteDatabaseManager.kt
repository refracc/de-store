package uk.ac.napier.sa.model

import uk.ac.napier.sa.controller.adt.Product
import java.sql.ResultSet

interface RemoteDatabaseManager {

    fun connect(host: String, port: Int, database: String, useSSL: Boolean, user: String, pass: String): Boolean {
        return false
    }

    fun disconnect(): Boolean {
        return false
    }

    fun init(path: String?): Boolean {
        return false
    }

    fun changePrice(id: Int, price: Double): Boolean {
        return false
    }

    fun sell(id: Int, saleType: Int): Boolean {
        return false
    }

    fun noStock(): Boolean {
        return false
    }

    fun purchase(customerId: Int, productId: Int): Boolean {
        return false
    }

    fun checkLoyaltyCardEligibility(id: Int): Boolean {
        return false
    }

    fun grantLoyalty(id: Int): Boolean {
        return false
    }

    fun printLastNPurchases(n: Int)
    fun getProduct(id: Int): Product? {
        return null
    }

    fun query(sql: String): ResultSet? {
        return null
    }

    fun retrieveProductName(id: Int): String? {
        return ""
    }

    fun stockMonitor(): List<Int?> {
        return emptyList<Int>()
    }

    fun generateReport(): Map<String?, Any?> {
        return emptyMap<String?, Any>()
    }
}