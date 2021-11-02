package uk.ac.napier.sa.model

import uk.ac.napier.sa.controller.adt.Product
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.RoundingMode
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.text.DecimalFormat
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class DatabaseManager private constructor() : RemoteDatabaseManager {

    private var conn: Connection? = null

    /**
     * Create a connection to the database.
     *
     * @param host     The host address of the machine.
     * @param port     The port allocated to the SQL database.
     * @param database The database to be used for the connection.
     * @param useSSL   Whether to use SSL for the connection.
     * @param user     The username.
     * @param pass     The password.
     * @return True if the database can be connected to, otherwise false.
     */
    override fun connect(
        host: String,
        port: Int,
        database: String,
        useSSL: Boolean,
        user: String,
        pass: String
    ): Boolean {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            return false
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://$host:$port/$database?useSSL=$useSSL", user, pass)
            println("Successfully connected to database!")
            return true
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * The method that has to be called to initialise the database.
     *
     * @param path The path of the file to be executed to initialise the database.
     * @return True: Initialisation is successful, false otherwise.
     */
    override fun init(path: String): Boolean {
        try {
            val lines = FileManager.instance!!.read(Path.of(path))
            for (s in lines!!) execute(s!!)
            return true
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Disconnect from the database.
     *
     * @return True if disconnect is successful, false if connection did not exist in the first place.
     */
    override fun disconnect(): Boolean {
        if (conn != null) {
            try {
                conn!!.close()
                return true
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * Query the database.
     *
     * @param sql The sql statement being used to query the database.
     * @return the set of results from the query.
     */
    override fun query(sql: String): ResultSet? {
        try {
            conn!!.prepareStatement(sql).use { return it.executeQuery() }
        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Update the database.
     *
     * @param sql The sql statement being used to update the database.
     * @return True: If the update could execute successfully, false otherwise.
     */
    private fun execute(sql: String): Boolean {
        try {
            conn!!.prepareStatement(sql).use { stmt -> return stmt.execute() }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Obtain product data from the database.
     *
     * @param id The identification number of the product
     * @return The product requested.
     */
    override fun getProduct(id: Int): Product? {
        val name: AtomicReference<String>? = null
        val stock = AtomicInteger()
        val price = AtomicReference(0.0)
        val sales: MutableList<Int> = ArrayList()
        try {
            var results = query("SELECT * FROM product WHERE ID = $id")
            while (true) {
                assert(results != null)
                if (!results!!.next()) break
                name!!.set(results.getString("name"))
                stock.set(results.getInt("stock"))
                price.set(results.getDouble("price"))
            }
            results = query("SELECT * FROM sales WHERE ID = $id")
            while (true) {
                assert(results != null)
                if (!results!!.next()) break
                sales.add(results.getInt("type"))
            }
            results.close()
            assert(name != null)
            return Product(id, name!!.get(), stock.get(), price.get(), sales)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Obtain a product's name from the database.
     *
     * @param id The ID of the product.
     * @return The name of the product.
     */
    override fun retrieveProductName(id: Int): String? {
        try {
            query(String.format("SELECT name FROM product WHERE ID = %d", id)).use { results ->
                assert(results != null)
                if (results!!.first()) {
                    return results.getString("name")
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Change the price of an item in the database.
     *
     * @param id    The id number of the product.
     * @param price The new price to be assigned to the item.
     * @return True if the price can be updated, false otherwise.
     */
    override fun changePrice(id: Int, price: Double): Boolean {
        try {
            BufferedReader(InputStreamReader(System.`in`)).use { input ->
                print(
                    """
    *************************************
    ***           ATTENTION           ***
    *************************************
    
    ---> Only managers can modify the prices of items.
    ** Unauthorised access to this system constitutes an offence
    ** under Section 1 of the Computer Misuse Act 1990.
    
    Manager username:
    """.trimIndent()
                )
                val username = input.readLine()
                print("\nManager password: ")
                val password = input.readLine()
                if (username.equals("StoreManager", ignoreCase = true) && password == "************") {
                    try {
                        query("SELECT * FROM product WHERE id = $id").use { results ->
                            assert(results != null)
                            if (results!!.next()) {
                                results.first()
                                results.updateDouble("price", price)
                                results.updateRow()
                                System.out.format("Product %d has had price updated to %f.2", id, price)
                                return true
                            }
                        }
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    }
                } else {
                    println(
                        """
    **************************
    ** Invalid Credentials! **
    **************************
    
    """.trimIndent()
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Create a new sale and add it to the database.
     *
     * @param id       The ID of the product
     * @param saleType The type of sale (1, 2, or 3)
     * @return True if the sale can be added to the database, false otherwise.
     */
    override fun sell(id: Int, saleType: Int): Boolean {
        return execute(String.format("INSERT INTO sale (`product`, `type`) VALUES (%d, %d)", id, saleType))
    }

    /**
     * Retrieve a list of stock that has a low quantity.
     * This function should be validated using the size of the collection returned,
     *
     * @return A list of stock that has a low quantity, if there isn't any, then an empty collection.
     */
    override fun stockMonitor(): List<Int?> {
        try {
            query("SELECT id FROM product WHERE stock <= 5").use { results ->
                val lowProducts: MutableList<Int?> = ArrayList()
                while (results!!.next()) {
                    lowProducts.add(results.getInt("id"))
                }
                return lowProducts
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return emptyList<Int>()
    }

    /**
     * Check the database for products that have no stock.
     * Then, given them a new stock value of a case of items (24x the product)
     *
     * @return True if there is no stock (order some more), false if no items have no stock.
     */
    override fun noStock(): Boolean {
        try {
            query("SELECT * FROM product WHERE stock = 0").use { results ->
                assert(results != null)
                if (results!!.next()) {
                    while (results.next()) {
                        results.updateInt("stock", 24)
                        results.updateRow()
                    }
                    return true
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Create a new purchase transaction and add it to the database.
     *
     * @param customerId The ID of the customer making the transaction.
     * @param productId  The ID of the product that is in use of the transaction.
     * @return True if the transaction is successful, false otherwise.
     */
    override fun purchase(customerId: Int, productId: Int): Boolean {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.UP
        var cost = 0.0
        if (execute("UPDATE product SET stock = stock - 1 WHERE id = $productId AND stock > 0")) {
            try {
                var results = query("SELECT price FROM product WHERE id = $productId")
                while (results?.next() == true) {
                    cost += results.getDouble("price")
                }
                System.out.printf("\nProduct price: £%s\n", df.format(cost))
                results = query("SELECT loyal FROM customer WHERE ID = $customerId")
                while (results!!.next()) {
                    if (results.getInt("loyalty") == 1) {
                        cost *= 0.9
                        println("Customer is on loyalty card scheme. 10% discount has been applied.")
                        System.out.printf("Discounted price: £%s\n", df.format(cost))
                    }
                }
                cost *= 1.05
                System.out.printf("Total cost: £%s\n", df.format(cost))
                var saleId = 0
                results = query("SELECT id FROM sale WHERE product = $productId ORDER BY id DESC")
                if (results!!.first()) saleId = results.getInt("id")
                results.close()
                return execute(
                    String.format(
                        "INSERT INTO transaction (`customer`, `sale`, `cost`) VALUES (%d, %d, %s)",
                        customerId,
                        saleId,
                        df.format(cost)
                    )
                )
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * Check if a customer is eligible for a loyalty card, based on purchases.
     *
     * @param id The ID of the customer.
     * @return True if the customer has had more than 2 purchases & not already on the loyalty scheme. False otherwise.
     */
    override fun checkLoyaltyCardEligibility(id: Int): Boolean {
        var purchaseCount = 0
        var customerCard = 0
        try {
            var results =
                query("SELECT COUNT(id) AS transactions FROM transaction WHERE CUSTOMER = $id")
            while (results!!.next()) {
                purchaseCount = results.getInt("transactions")
            }
            results = query("SELECT loyal FROM customer WHERE ID = $id")

            while (results!!.next()) {
                customerCard = results.getInt("loyal")
            }
            return (purchaseCount > 2) && (customerCard != 1)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Grant a customer a loyalty card.
     *
     * @param id The ID of the customer.
     * @return True if the database transaction has been complete. False otherwise.
     */
    override fun grantLoyalty(id: Int): Boolean {
        return execute("UPDATE customer SET loyal = 1 WHERE id = $id")
    }

    /**
     * Produce a report of that month's operations.
     *
     * @return A map of purchases, revenue and popular items.
     */
    override fun generateReport(): Map<String?, Any?> {
        val stats: MutableMap<String?, Any?> = HashMap(emptyMap<String?, Any>())
        var purchases = 0
        var revenue = 0.0
        var popularItem = 0
        try {
            var results = query("SELECT COUNT(id) FROM transaction WHERE purchased > NOW() - INTERVAL 1 MONTH")!!
            if (results.first()) {
                purchases = results.getInt(1)
            }
            results = query("SELECT SUM(cost) FROM transaction WHERE purchased > NOW() - INTERVAL 1 MONTH")!!
            if (results.first()) {
                revenue = results.getDouble(1)
            }
            results =
                query("SELECT MAX(product_count) FROM (SELECT product, COUNT(product) AS product_count FROM transaction GROUP BY product) AS alias")!!
            if (results.first()) {
                popularItem = results.getInt(1)
            }
            results.close()
            stats["purchases"] = purchases
            stats["revenue"] = revenue
            stats["most-popular"] = popularItem
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return stats
    }

    /**
     * A function used to print the last N purchases from the database.
     *
     * @param n The number of purchases to see.
     */
    override fun printLastNPurchases(n: Int) {
        try {
            query(
                "SELECT id, " +
                        "(SELECT name FROM product WHERE id = transaction.id) as product_name, " +
                        "(SELECT name FROM customer WHERE id = transaction.customer) AS customer_id, " +
                        "cost, purchased FROM transaction ORDER BY purchased DESC LIMIT " + n
            ).use { results ->
                while (true) {
                    assert(results != null)
                    if (!results!!.next()) break
                    System.out.printf(
                        """
    +------------------------------------+
    | Transaction ID: %s			|
    | Product Name: %s			|
    | Customer ID: %s			|
    | Cost (£): %s			|
    | Purchased: %s			|
    +-------------------------------------+
    
    """.trimIndent(),
                        results.getInt("id"), results.getString("product_name"), results.getInt("customer_id"),
                        results.getDouble("cost"), results.getTimestamp("purchased")
                    )
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * A thread-safe way of instantiating the [DatabaseManager] class.
         *
         * @return An instance of this class.
         */
        @JvmStatic
        @get:Synchronized
        @Volatile
        var instance: DatabaseManager? = null
            get() {
                if (field == null) {
                    synchronized(DatabaseManager::class.java) {
                        if (field == null) {
                            field = DatabaseManager()
                        }
                    }
                }
                return field
            }
            private set
    }
}