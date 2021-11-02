package uk.ac.napier.sa.controller.adt

import java.text.DecimalFormat
import java.math.RoundingMode
import java.lang.StringBuilder
import java.util.ArrayList

/**
 * This class is used to represent all products in a ORM-based fashion.
 */
class Product {
    /**
     * Obtain the product ID number.
     *
     * @return The product ID.
     */
    private var id = 0

    /**
     * Obtain the product name.
     *
     * @return The product name.
     */
    private var name: String? = null

    /**
     * Obtain the product quantity
     *
     * @return The quantity of the product
     */
    private var quantity = 0

    /**
     * Obtain the price per unit
     *
     * @return The price per unit
     */
    private var price = 0.0

    private var sales: List<Int>? = null

    private constructor() {}

    /**
     * The default constructor for this class.
     *
     * @param id       The identification number allocated to the product from the database.
     * @param name     The name of the product.
     * @param quantity The quantity of the item.
     * @param price    The price of the item.
     * @param sales    The list of sales (containing the sales type integer) with this item.
     */
    constructor(id: Int, name: String?, quantity: Int, price: Double, sales: List<Int?>?) {
        this.id = id
        this.name = name
        this.quantity = quantity
        this.price = price
        this.sales = (sales ?: ArrayList()) as List<Int>?
    }

    /**
     * The list of sale types attributed to this item.
     *
     * @return A copy of the list of sale types.
     */
    private fun getSales(): String {
        return sales!!.toTypedArray().contentToString()
    }

    override fun toString(): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.UP
        return String.format(
            StringBuilder().append("Product (ID): %d\n").append("Product (Name): %s\n")
                .append("Product (Price): £%s/unit\n").append("Product (Stock): %d\n").append("Product (Sales): %s\n")
                .toString(),
            id, name, df.format(price), quantity,
            if (getSales().contains("1")) "3 for 2" else if (getSales().contains("2")) "Buy One Get One Free" else if (getSales().contains(
                    "3"
                )
            ) "Free Delivery" else "N/A"
        )
    }
}