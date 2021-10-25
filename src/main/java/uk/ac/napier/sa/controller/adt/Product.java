package uk.ac.napier.sa.controller.adt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to represent all products in a ORM-based fashion.
 */
public class Product {

    private int id;
    private String name;
    private int quantity;
    private double price;
    private List<Integer> sales;

    private Product() {
    }

    /**
     * The default constructor for this class.
     *
     * @param id       The identification number allocated to the product from the database.
     * @param name     The name of the product.
     * @param quantity The quantity of the item.
     * @param price    The price of the item.
     * @param sales    The list of sales (containing the sales type integer) with this item.
     */
    public Product(int id, String name, int quantity, double price, List<Integer> sales) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.sales = ((sales == null) ? new ArrayList<>() : sales);
    }

    /**
     * Obtain the product ID number.
     *
     * @return The product ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtain the product name.
     *
     * @return The product name.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtain the product quantity
     *
     * @return The quantity of the product
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Obtain the price per unit
     *
     * @return The price per unit
     */
    public double getPrice() {
        return price;
    }

    /**
     * The list of sale types attributed to this item.
     *
     * @return A copy of the list of sale types.
     */
    public String getSales() {
        return Arrays.toString(sales.toArray());
    }

    @Override
    public String toString() {
        return String.format("""
                        Product (ID): %d
                        Product (Name): %s
                        Product (Price): £%f/unit
                        Product (Stock): %d
                        Product (Sales): %s
                        """,
                this.getId(), this.getName(), this.getPrice(), this.getQuantity(),
                this.getSales().contains("1") ? "3 for 2" : this.getSales().contains("2") ? "Buy One Get One Free" : this.getSales().contains("3") ? "Free Delivery" : "N/A");
    }
}
