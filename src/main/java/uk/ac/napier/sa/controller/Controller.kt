package uk.ac.napier.sa.controller;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.ac.napier.sa.controller.adt.Product;
import uk.ac.napier.sa.model.RemoteDatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public final class Controller {

    private final RemoteDatabaseManager rdbm;

    public Controller (RemoteDatabaseManager rdbm) {
        this.rdbm = rdbm;
    }
    /**
     * Obtain relevant product information.
     *
     * @param id The ID of the product
     * @return The product name.
     */
    public String retrieveProduct(int id) {
        Product p = rdbm.getProduct(id);
        return p.toString();
    }

    /**
     * Change the price of an item.
     * ** REQUIRES MANAGER AUTHORISATION **
     *
     * @param id       The ID of the item.
     * @param newPrice The new price of the item.
     * @return Whether the price has been updated.
     */
    public @NotNull String changePrice(int id, double newPrice) {
        return rdbm.changePrice(id, newPrice) ? "Price updated successfully." : "[!] Price has not been updated.";
    }

    /**
     * Create a purchase.
     *
     * @param customer The customer ID
     * @param product  The {@link Product} ID.
     * @return Whether the purchase has been allowed.
     */
    public @NotNull String purchase(int customer, int product) {
        return rdbm.purchase(customer, product) ? "Purchase has been confirmed." : "[!] Purchase disallowed.";
    }

    /**
     * Enrol a customer on the Loyalty Card scheme.
     *
     * @param id The ID of the customer.
     * @return Whether the customer was added to the scheme
     */
    public String enrolOnLoyaltyCardScheme(int id) {
        if (rdbm.checkLoyaltyCardEligibility(id)) {
            System.out.printf("\nCustomer %d is eligible for a Loyalty Card!\n", id);
            System.out.println("""
                    Do you want to place the customer on this scheme?
                    [Y] Yes
                    [N] No
                                        
                    Enter your choice: """);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                String selection = br.readLine();
                if (selection.equalsIgnoreCase("y")) {
                    rdbm.grantLoyalty(id);
                    return String.format("Customer %d has been placed on Loyalty Card scheme!", id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Customer not placed on loyalty scheme.";
    }

    /**
     * Check if the user wants to take this product out on finance.
     *
     * @param choice The user's choice.
     * @return Options for the user if they have opted for finance.
     */
    @Contract(pure = true)
    public @NotNull String finance(char choice) {
        return (choice == 'Y') ? "Please check out with our provider, Klarna, for more information: https://www.klarna.com/uk/business/products/financing/"
        : "[!] You have not opted in for financing.";
    }

    /**
     * Print the last N purchases from the database.
     *
     * @param n The amount of purchases.
     */
    public void printLastNPurchases(int n) {
        rdbm.printLastNPurchases(n);
    }

    /**
     * Generate a report for the user of the system.
     *
     * @return A report for that month.
     */
    public String generateReport() {
        Map<String, Object> map = rdbm.generateReport();

        if (map.size() > 0) {
            return String.format("""
                            MONTHLY REPORT
                            Total purchases made in last month: %d
                            Total revenue in last month: %f
                            Most popular item from last month: %s
                            """,
                    Integer.parseInt((String) map.get("purchases")),
                    Double.parseDouble((String) map.get("revenue")),
                    map.get("most-popular"));
        }
        return "No report to be generated.";
    }
}
