package uk.ac.napier.sa;

import uk.ac.napier.sa.model.DatabaseManager;

public class Main {

    public static void main(String[] args) {
        DatabaseManager.getInstance().connect("::1", 3306, "store", false, "root", "admin123");
        DatabaseManager.getInstance().init("src/schema.sql");
        DatabaseManager.getInstance().init("src/data.sql");
    }
}
