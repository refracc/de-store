package uk.ac.napier.sa.controller

import org.jetbrains.annotations.Contract
import uk.ac.napier.sa.controller.adt.Product
import uk.ac.napier.sa.model.RemoteDatabaseManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.function.Consumer

class Controller(private val rdbm: RemoteDatabaseManager) : RemoteController {
    /**
     * Obtain relevant product information.
     *
     * @param id The ID of the product
     * @return The product name.
     */
    override fun retrieveProduct(id: Int): String {
        val p = rdbm.getProduct(id)
        return Objects.requireNonNull(p).toString()
    }

    /**
     * Change the price of an item.
     * ** REQUIRES MANAGER AUTHORISATION **
     *
     * @param id       The ID of the item.
     * @param newPrice The new price of the item.
     * @return Whether the price has been updated.
     */
    override fun changePrice(id: Int, newPrice: Double): String {
        return if (rdbm.changePrice(id, newPrice)) "Price updated successfully." else "[!] Price has not been updated."
    }

    /**
     * Apply a sale type to an item.
     * @param id The ID of the item.
     * @param sale The sale type.
     * @return True if the sale can be applied to the item. False otherwise.
     */
    override fun addSale(id: Int, sale: Int): String {
        println("Selected sale type: $sale")
        return if (rdbm.sell(id, sale)) "Sale type added successfully." else "[!] Could not apply sale to item."
    }

    /**
     * Monitor the stock quantity.
     * @return A
     */
    override fun stockMonitor(): String {
        val lowStock = rdbm.stockMonitor()
        if (rdbm.noStock()) {
            println("Items that are out of stock have been ordered.")
        }
        if (lowStock.isEmpty()) {
            return ""
        } else {
            println("WARNING: The following products are low in stock (by ID):")
            lowStock.forEach(Consumer { x: Int? -> println(x) })
        }
        return "Stock check completed."
    }

    /**
     * Create a purchase.
     *
     * @param customer The customer ID
     * @param product  The [Product] ID.
     * @return Whether the purchase has been allowed.
     */
    override fun purchase(customer: Int, product: Int): String {
        return if (rdbm.purchase(customer, product)) "Purchase has been confirmed." else "[!] Purchase disallowed."
    }

    /**
     * Enrol a customer on the Loyalty Card scheme.
     *
     * @param id The ID of the customer.
     * @return Whether the customer was added to the scheme
     */
    override fun enrolOnLoyaltyCardScheme(id: Int): String {
        if (rdbm.checkLoyaltyCardEligibility(id)) {
            System.out.printf("\nCustomer %d is eligible for a Loyalty Card!\n", id)
            println(
                """
    Do you want to place the customer on this scheme?
    [Y] Yes
    [N] No
    
    Enter your choice: 
    """.trimIndent()
            )
            try {
                BufferedReader(InputStreamReader(System.`in`)).use { br ->
                    val selection = br.readLine()
                    if (selection.equals("y", ignoreCase = true)) {
                        rdbm.grantLoyalty(id)
                        return String.format("Customer %d has been placed on Loyalty Card scheme!", id)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return "Customer not placed on loyalty scheme."
    }

    /**
     * Check if the user wants to take this product out on finance.
     *
     * @param choice The user's choice.
     * @return Options for the user if they have opted for finance.
     */
    @Contract(pure = true)
    override fun finance(choice: Int): String {
        return if (choice == 1) "Please check out with our provider, Klarna, for more information: https://www.klarna.com/uk/business/products/financing/" else "[!] You have not opted in for financing."
    }

    /**
     * Print the last N purchases from the database.
     *
     * @param n The amount of purchases.
     */
    override fun printLastNPurchases(n: Int) {
        rdbm.printLastNPurchases(n)
    }

    /**
     * Generate a report for the user of the system.
     *
     * @return A report for that month.
     */
    override fun generateReport(): String {
        val map = rdbm.generateReport()
        return if (map.isNotEmpty()) {
            String.format(
                """
                MONTHLY REPORT
                Total purchases made in last month: %d
                Total revenue in last month: %f
                Most popular item from last month: %s
                
                """.trimIndent(),
                map["purchases"] as String?,
                map["revenue"] as String?,
                map["most-popular"]
            )
        } else "No report to be generated."
    }
}