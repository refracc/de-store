package uk.ac.napier.sa.controller;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.ac.napier.sa.controller.adt.Product;
import uk.ac.napier.sa.model.RemoteDatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record Controller(RemoteDatabaseManager rdbm) implements RemoteController {

    /**
     * Obtain relevant product information.
     *
     * @param id The ID of the product
     * @return The product name.
     */
    @Override
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
    @Override
    public @NotNull
    String changePrice(int id, double newPrice) {
        return rdbm.changePrice(id, newPrice) ? "Price updated successfully." : "[!] Price has not been updated.";
    }

    /**
     * Create a purchase.
     *
     * @param customer The customer ID
     * @param product  The {@link Product} ID.
     * @return Whether the purchase has been allowed.
     */
    @Override
    public @NotNull
    String purchase(int customer, int product) {
        return rdbm.purchase(customer, product) ? "Purchase has been confirmed." : "[!] Purchase disallowed.";
    }

    /**
     * Enrol a customer on the Loyalty Card scheme.
     *
     * @param id The ID of the customer.
     * @return Whether the customer was added to the scheme
     */
    @Override
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
        return "Customer not placed on loyalty scheme.\n";
    }

    public String stockMonitor() {
        List<Integer> lowStock = rdbm.stockMonitor();

        if (rdbm.noStock()) {
            System.out.println("Items that are out of stock have been ordered.");
        }

        if (!lowStock.isEmpty()) {
            System.out.println("The following products are low in stock (by ID):");
            lowStock.forEach(System.out::println);
        }
        return "Stock check completed.\n";
    }

    public String addSale(int id, int sale) {
        System.out.println("Selected sale type " + sale);
        return (rdbm.sell(id, sale) ? "Sale type added successfully\n" : "[!] Could not apply sale to this item.\n");
    }

    /**
     * Check if the user wants to take this product out on finance.
     *
     * @param choice The user's choice.
     * @return Options for the user if they have opted for finance.
     */
    @Override
    @Contract(pure = true)
    public @NotNull
    String finance(char choice) {
        return (choice == 'Y') ? "Please check out with our provider, Klarna, for more information: https://www.klarna.com/uk/business/products/financing/"
                : "[!] You have not opted in for financing.";
    }

    /**
     * Print the last N purchases from the database.
     *
     * @param n The amount of purchases.
     */
    @Override
    public void printLastNPurchases(int n) {
        rdbm.printLastNPurchases(n);
    }

    /**
     * Generate a report for the user of the system.
     *
     * @return A report for that month.
     */
    @Override
    public String generateReport() {
        Map<String, Object> map = rdbm.generateReport();

        if (map.size() > 0) {
            return String.format("""
                            MONTHLY REPORT
                            Total purchases made in last month: %s
                            Total revenue in last month: %s
                            Most popular item from last month: %s
                            """,
                    map.get("purchases"),
                    map.get("revenue"),
                    map.get("most-popular"));
        }
        return "No report to be generated.";
    }
}
