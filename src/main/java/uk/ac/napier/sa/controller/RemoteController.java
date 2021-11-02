package uk.ac.napier.sa.controller;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.ac.napier.sa.model.RemoteDatabaseManager;

public interface RemoteController {
    String retrieveProduct(int id);

    @NotNull
    String changePrice(int id, double newPrice);

    @NotNull
    String purchase(int customer, int product);

    String enrolOnLoyaltyCardScheme(int id);

    @Contract(pure = true)
    @NotNull
    String finance(char choice);

    void printLastNPurchases(int n);

    String generateReport();

    RemoteDatabaseManager rdbm();

    String stockMonitor();

    String addSale(int id, int selected);
}
