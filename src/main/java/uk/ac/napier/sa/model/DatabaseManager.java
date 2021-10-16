package uk.ac.napier.sa.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.ac.napier.sa.controller.adt.Product;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
     * Asynchronously obtain
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
                ResultSet results = query("SELECT * FROM products WHERE ID = " + id);

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

                assert name != null;
                return new Product(id, name.get(), stock.get(), price.get(), sales);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });

        return future.join();
    }
}
