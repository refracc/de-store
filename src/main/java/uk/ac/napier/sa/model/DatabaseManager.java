package uk.ac.napier.sa.model;

import java.sql.*;

public final class DatabaseManager {

    private volatile static DatabaseManager instance;
    private Connection conn;

    private DatabaseManager() {
    }

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

    public boolean connect(String host, int port, String database, boolean useSSL, String user, String pass) {
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

    public ResultSet query(String sql) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateCustomer(int id, String name, boolean loyal) {
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

    public boolean updateProduct(int id, String name, int stock, double price) {
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

    public boolean updateTransaction(int id, int product, int customer, int sale, double cost, Timestamp time) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE transaction SET product = ?, customer = ?, sale = ?, cost = ?, purchased = ? WHERE ID = " + id + ";"
            );
            stmt.setInt(1, product);
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
}
