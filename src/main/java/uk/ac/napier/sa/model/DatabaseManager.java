package uk.ac.napier.sa.model;

import java.sql.*;

public final class DatabaseManager {

    private static DatabaseManager instance;
    private Connection conn;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public boolean connect(String host, int port, boolean useSSL, String user, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/database?useSSL=" + useSSL, user, pass);
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
                    "UPDATE customer SET name = ?, loyal = ? WHERE id = " + id);
            stmt.setString(1, name);
            stmt.setBoolean(2, loyal);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
