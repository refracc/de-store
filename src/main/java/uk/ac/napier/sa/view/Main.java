package uk.ac.napier.sa.view;

import uk.ac.napier.sa.controller.Controller;
import uk.ac.napier.sa.model.DatabaseManager;
import uk.ac.napier.sa.model.RemoteDatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        DatabaseManager.getInstance().connect("::1", 3306, "store", false, "root", "admin123");
        DatabaseManager.getInstance().init("src/schema.sql");
        DatabaseManager.getInstance().init("src/data.sql");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
            RemoteDatabaseManager rdbm = DatabaseManager.getInstance();
            Controller c = new Controller(rdbm);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void menu() {
        System.out.print("""
                +----------+-------------------------------+
                | DE-Store | Distributed Management System |
                +----------+-------------------------------+
                |   [1]    |   Obtain Product Details      |
                |   [2]    |   PLU Modification            |
                |   [3]    |   Apply Product Sale          |
                |   [4]    |   Sell Product                |
                |   [5]    |   Everything Loyalty          |
                |   [6]    |   Analysis & Reports          |
                +----------+-------------------------------+
                
                Enter choice: """);
    }


}
