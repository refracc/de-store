package uk.ac.napier.sa.view;

import uk.ac.napier.sa.controller.Controller;
import uk.ac.napier.sa.controller.RemoteController;
import uk.ac.napier.sa.model.DatabaseManager;
import uk.ac.napier.sa.model.RemoteDatabaseManager;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private static final Scanner s = new Scanner(System.in);
    private static final RemoteDatabaseManager rdbm = DatabaseManager.getInstance();
    private static final RemoteController c;

    static {
        assert rdbm != null;
        c = new Controller(rdbm);
    }

    public static void main(String[] args) {
        Objects.requireNonNull(DatabaseManager.getInstance()).connect("::1", 3306, "store", false, "root", "admin123");
        Arrays.asList("src/schema.sql", "src/data.sql").forEach(DatabaseManager.getInstance()::init);
        int choice = 0;

        while (choice != 7) {
            System.out.println(c.stockMonitor());
            menu();
            choice = s.nextInt();

            switch (choice) {
                case 1 -> obtainProductInfo();
                case 2 -> modifyPLU();
                case 3 -> addSale();
                case 4 -> purchase();
                case 5 -> enrolOnLoyaltyScheme();
                case 6 -> generateReport();
                case 7 -> System.out.println();
            }
        }
        DatabaseManager.getInstance().disconnect();
    }

    private static void menu() {
        System.out.print("+----------+-------------------------------+\n" +
                         "| DE-Store | Distributed Management System |\n" +
                         "+----------+-------------------------------+\n" +
                         "|   [1]    |   Obtain Product Details      |\n" +
                         "|   [2]    |   PLU Modification            |\n" +
                         "|   [3]    |   Apply Product Sale          |\n" +
                         "|   [4]    |   Sell Product                |\n" +
                         "|   [5]    |   Everything Loyalty          |\n" +
                         "|   [6]    |   Analysis & Reports          |\n" +
                         "|   [7]    |   Quit Application            |\n" +
                         "+----------+-------------------------------+\n" +
                         "\n" +
                         "Enter choice: ");
    }

    private static void obtainProductInfo() {
        System.out.print("Please provide a product ID: ");
        int id = s.nextInt();
        System.out.printf("\n%s\n", c.retrieveProduct(id));
    }

    private static void modifyPLU() {
        System.out.print("Please enter the ID of the item whose price you wish to modify: ");
        int id = s.nextInt();

        System.out.print("\nPlease enter the new price of the item: ");
        double price = s.nextDouble();

        System.out.printf("\n%s", c.changePrice(id, price));
    }

    private static void addSale() {
        System.out.print("Please enter the ID of the item you wish to apply sales to: ");
        int id = s.nextInt();
        int selected = 0;

        do {
            System.out.print("Please select the sale type for this item:\n" +
                             "[1] 3 for the price of 2\n" +
                             "[2] Buy One Get One Free\n" +
                             "[3] Free delivery.\n" +
                             "Please enter your choice: ");
            selected = s.nextInt();
        } while (!Arrays.asList(1, 2, 3).contains(selected));


        System.out.println(c.addSale(id, selected));
    }

    private static void purchase() {
        System.out.print("Please enter the ID of the customer purchasing: ");
        int customer = s.nextInt();

        System.out.print("\nPlease enter the ID of the product you wish to purchase: ");
        int product = s.nextInt();

        System.out.printf("\n%s", c.purchase(customer, product));
        int choice = 0;

        do {
            System.out.print("\nDoes the customer wish to opt in for the available finance options?\n" +
                             "[1] Yes\n" +
                             "[2] No\n" +
                             "Please enter your choice: ");
            choice = s.nextInt();
        } while (!Arrays.asList(1, 2).contains(choice));

        System.out.printf("\n%s", c.finance((char) choice));
    }

    private static void enrolOnLoyaltyScheme() {
        System.out.print("Please enter a customer ID: ");
        int id = s.nextInt();

        System.out.printf("\n%s", c.enrolOnLoyaltyCardScheme(id));
    }

    private static void generateReport() {
        System.out.println(c.generateReport());
        int choice = 0;

        do {
            System.out.println("\nPrint previous N purchases?");
            System.out.println("[1] Yes\n" +
                               "[2] No\n" +
                               "Enter your choice: ");
            choice = s.nextInt();
        } while (!Arrays.asList(1, 2).contains(choice));

        if (choice == 1) {
            System.out.print("\n Please pick an amount of previous purchases to print: ");
            choice = s.nextInt();
            c.printLastNPurchases(choice);
        }

        System.out.println("\n");
    }
}
