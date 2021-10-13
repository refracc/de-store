package uk.ac.napier.sa.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
     */
    private @Nullable ResultSet query(@NotNull String sql) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
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
     */
    private boolean execute(@NotNull String sql) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            return stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Method used to update a customer by ID.
     *
     * @param id    The unique identifier allocated to all Customers.
     * @param name  The name of the customer
     * @param loyal If the customer holds a valid loyalty card
     * @return True if the update is successful.
     */
    public boolean updateCustomer(int id, @NotNull String name, boolean loyal) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE customer SET name = ?, loyal = ? WHERE id = " + id + ";"
            );
            stmt.setString(1, name);
            stmt.setBoolean(2, loyal);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * The method used to update a product by ID
     *
     * @param id    The unique identifier of the product.
     * @param name  The name of the product.
     * @param stock The stock quantity.
     * @param price The price of the item.
     * @return True if the update is successful.
     */
    public boolean updateProduct(int id, @NotNull String name, int stock, double price) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE product SET name = ?, stock = ?, price = ? WHERE id = " + id + ";"
            );
            stmt.setString(1, name);
            stmt.setInt(2, stock);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * The method used to update a sale.
     *
     * @param id      The unique identifier of the sale.
     * @param product The product purchased.
     * @param type    The type of sale.
     * @return True if the sale is updated successfully, false otherwise.
     */
    public boolean updateSale(int id, int product, int type) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE sale SET product = ?, type = ? WHERE id = " + id + ";"
            );
            stmt.setInt(1, product);
            stmt.setInt(2, type);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * The method used for udpateing a transaction.
     *
     * @param id       The unique identifier for the transaction.
     * @param customer The customer's ID
     * @param sale     The sale type.
     * @param cost     The total cost of the transaction.
     * @param time     The date & time this transaction occurred.
     * @return True if the transaction has been updated accordingly, false otherwise.
     */
    public boolean updateTransaction(int id, int customer, int sale, double cost, @NotNull Timestamp time) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE transaction SET customer = ?, sale = ?, cost = ?, purchased = ? WHERE ID = " + id + ";"
            );
            stmt.setInt(2, customer);
            stmt.setInt(3, sale);
            stmt.setDouble(4, cost);
            stmt.setTimestamp(5, time);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean init(String p) {
        try {
            List<String> lines = FileManager.getInstance().read(Path.of(p));
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                for (String s : lines) execute(s);
                return null;
            });
            future.get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
