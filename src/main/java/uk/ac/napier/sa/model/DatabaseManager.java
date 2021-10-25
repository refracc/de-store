package uk.ac.napier.sa.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.ac.napier.sa.controller.adt.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class DatabaseManager {

    private volatile static DatabaseManager instance;
    private Connection conn;

    private DatabaseManager() {
    }

    /**
     * A thread-safe way of instantiating the {@link DatabaseManager} class.
     *
     * @return An instance of this class.
     */
    public synchronized static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

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
    public boolean connect(@NotNull String host, int port, @NotNull String database, boolean useSSL, @NotNull String user, @NotNull String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL, user, pass);
            System.out.println("Successfully connected to database!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Disconnect from the database.
     *
     * @return True if disconnect is successful, false if connection did not exist in the first place.
     */
    public boolean disconnect() {
        if (conn != null) {
            try {
                conn.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Query the database.
     *
     * @param sql The sql statement being used to query the database.
     * @return the set of results from the query.
     */
    private @Nullable ResultSet query(@NotNull String sql) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update the database.
     *
     * @param sql The sql statement being used to update the database.
     * @return True: If the update could execute successfully, false otherwise.
     */
    private boolean execute(@NotNull String sql) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * The method that has to be called to initialise the database.
     *
     * @param p The path of the file to be executed to initialise the database.
     * @return True: Initialisation is successful, false otherwise.
     */
    public boolean init(String p) {
        try {
            List<String> lines = FileManager.getInstance().read(Path.of(p));
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                for (String s : lines) execute(s);
                return null;
            });
            future.join();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Asynchronously obtain product data from the database.
     *
     * @param id The identification number of the product
     * @return The product requested.
     */
    public Product getProduct(int id) {
        AtomicReference<String> name = null;
        AtomicInteger stock = new AtomicInteger();
        AtomicReference<Double> price = new AtomicReference<>((double) 0);
        List<Integer> sales = new ArrayList<>();

        CompletableFuture<Product> future = CompletableFuture.supplyAsync(() -> {
            try {
                ResultSet results = query("SELECT * FROM product WHERE ID = " + id);

                while (true) {
                    assert results != null;
                    if (!results.next()) break;
                    name.set(results.getString("name"));
                    stock.set(results.getInt("stock"));
                    price.set(results.getDouble("price"));
                }

                results = query("SELECT * FROM sales WHERE ID = " + id);

                while (true) {
                    assert results != null;
                    if (!results.next()) break;
                    sales.add(results.getInt("type"));
                }

                results.close();

                assert name != null;
                return new Product(id, name.get(), stock.get(), price.get(), sales);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });

        return future.join();
    }

    /**
     * Obtain a product's name from the database.
     * @param id The ID of the product.
     * @return The name of the product.
     */
    public @Nullable String retrieveProductName(int id) {
        try (ResultSet results = query(String.format("SELECT name FROM product WHERE ID = %d", id))) {

            assert results != null;
            if (results.first()) {
                return results.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Change the price of an item in the database.
     *
     * @param id    The id number of the product.
     * @param price The new price to be assigned to the item.
     * @return True if the price can be updated, false otherwise.
     */
    public boolean changePrice(int id, double price) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("""
                    *************************************
                    ***           ATTENTION           ***
                    *************************************
                                        
                    ---> Only managers can modify the prices of items.
                    ** Unauthorised access to this system constitutes an offence
                    ** under Section 1 of the Computer Misuse Act 1990.
                                        
                    Manager username: """);
            String username = input.readLine();
            System.out.print("\nManager password: ");
            String password = input.readLine();

            if (username.equalsIgnoreCase("StoreManager") && password.equals("************")) {
                try (ResultSet results = query("SELECT * FROM product WHERE id = " + id)) {
                    assert results != null;
                    if (results.next()) {
                        results.first();
                        results.updateDouble("price", price);
                        results.updateRow();
                        System.out.format("Product %d has had price updated to %f.2", id, price);
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("""
                        **************************
                        ** Invalid Credentials! **
                        **************************
                        """);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create a new sale and add it to the database.
     *
     * @param id       The ID of the product
     * @param saleType The type of sale (1, 2, or 3)
     * @return True if the sale can be added to the database, false otherwise.
     */
    public boolean sell(int id, int saleType) {
        return execute(String.format("INSERT INTO sale (`product`, `type`) VALUES (%d, %d)", id, saleType));
    }

    public @NotNull List<Integer> stockMonitor() {
        try (ResultSet results = query("SELECT id FROM product WHERE stock <= 5")) {
            List<Integer> lowProducts = new ArrayList<>();

            while (true) {
                assert results != null;
                if (!results.next()) break;
                lowProducts.add(results.getInt("id"));
            }
            return lowProducts;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Check the database for products that have no stock.
     * Then, given them a new stock value of a case of items (24x the product)
     *
     * @return True if there is no stock (order some more), false if no items have no stock.
     */
    public boolean noStock() {
        try (ResultSet results = query("SELECT * FROM product WHERE stock = 0")) {
            assert results != null;
            if (results.next()) {
                while (results.next()) {
                    results.updateInt("stock", 24);
                    results.updateRow();
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create a new purchase transaction and add it to the database.
     * @param customerId The ID of the customer making the transaction.
     * @param productId The ID of the product that is in use of the transaction.
     * @return True if the transaction is successful, false otherwise.
     */
    public boolean purchase(int customerId, int productId) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.UP);

        double cost = 0;

        if (execute(String.format("UPDATE product SET stock = stock - 1 WHERE id = %d AND stock > 0", productId))) {
            try {
                ResultSet results = query(String.format("SELECT price FROM product WHERE id = %d", productId));
                while (true) {
                    assert results != null;
                    if (!results.next()) break;
                    cost += results.getDouble("price");
                }

                System.out.printf("\nProduct price: £%s\n", df.format(cost));

                results = query(String.format("SELECT loyal FROM customer WHERE ID = %d", customerId));

                while (true) {
                    assert results != null;
                    if (!results.next()) break;
                    if (results.getInt("loyalty") == 1) {
                        cost *= 0.9;
                        System.out.println("Customer is on loyalty card scheme. 10% discount has been applied.");
                        System.out.printf("Discounted price: £%s\n", df.format(cost));
                    }
                }

                cost *= 1.05;
                System.out.printf("Total cost: £%s\n", df.format(cost));

                int saleId = 0;
                results = query("SELECT id FROM sale WHERE product = " + productId + " ORDER BY id DESC");
                assert results != null;

                if (results.first())
                    saleId = results.getInt("id");

                results.close();
                return execute(String.format("INSERT INTO transaction (`customer`, `sale`, `cost`) VALUES (%d, %d, %s)", customerId, saleId, df.format(cost)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Check if a customer is eligible for a loyalty card, based on purchases.
     * @param id The ID of the customer.
     * @return True if the customer has had more than 2 purchases & not already on the loyalty scheme. False otherwise.
     */
    public boolean checkLoyaltyCardEligibility(int id) {
        int purchaseCount = 0;
        int customerCard = 0;

        try {
            ResultSet results = query(String.format("SELECT COUNT(id) AS transactions FROM transaction WHERE CUSTOMER = %d", id));

            while (true) {
                assert results != null;
                if (!results.next()) break;
                purchaseCount = results.getInt("transactions");
            }

            results = query(String.format("SELECT loyal FROM customer WHERE ID = %d", id));
            assert results != null;

            if (results.first()) {
                customerCard = results.getInt("loyal");
            }

            return ((purchaseCount > 2) && (customerCard != 1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Grant a customer a loyalty card.
     * @param id The ID of the customer.
     * @return True if the database transaction has been complete. False otherwise.
     */
    public boolean grantLoyalty(int id) {
        return execute(String.format("UPDATE customer SET loyal = 1 WHERE id = %d", id));
    }

    /**
     * Produce a report of that month's operations.
     * @return A map of purchases, revenue and popular items.
     */
    public @NotNull Map<String, Object> generateReport() {
        Map<String, Object> stats = new HashMap<>();

        int purchases = 0;
        double revenue = 0;
        int popularItem = 0;

        try {
            ResultSet results = query("SELECT COUNT(id) FROM transaction WHERE purchased > NOW() - INTERVAL 1 MONTH");

            assert results != null;
            if (results.first()) {
                purchases = results.getInt(1);
            }

            results = query("SELECT SUM(cost) FROM transaction WHERE purchased > NOW() - INTERVAL 1 MONTH");

            assert results != null;
            if (results.first()) {
                revenue = results.getDouble(1);
            }

            results = query("SELECT MAX(product_count) FROM (SELECT product, COUNT(product) AS product_count FROM transaction GROUP BY product) AS alias");

            assert results != null;
            if (results.first()) {
                popularItem = results.getInt(1);
            }

            results.close();

            stats.put("purchases", purchases);
            stats.put("revenue", revenue);
            stats.put("most-popular", popularItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public void printLastNPurchases(int n) {
        try (ResultSet results = query("SELECT id, " +
                "(SELECT name FROM product WHERE id = transaction.id) as product_name, " +
                "(SELECT name FROM customer WHERE id = transaction.customer) AS customer_id, " +
                "cost, purchased FROM transaction ORDER BY purchased DESC LIMIT " + n)) {

            while (true) {
                assert results != null;
                if (!results.next()) break;
                System.out.printf("""
                        +------------------------------------+
                        | Transaction ID: %s\t\t\t|
                        | Product Name: %s\t\t\t|
                        | Customer ID: %s\t\t\t|
                        | Cost (£): %s\t\t\t|
                        | Purchased: %s\t\t\t|
                        +-------------------------------------+
                        """,
                        results.getInt("id"), results.getString("product_name"), results.getInt("customer_id"),
                        results.getDouble("cost"), results.getTimestamp("purchased"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}