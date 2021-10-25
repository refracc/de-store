package uk.ac.napier.sa.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.ac.napier.sa.controller.adt.Product;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public sealed interface RemoteDatabaseManager permits DatabaseManager {


    boolean connect(@NotNull String host, int port, @NotNull String database, boolean useSSL, @NotNull String user, @NotNull String pass);

    boolean disconnect();

    boolean init(String path);

    boolean changePrice(int id, double price);

    boolean sell(int id, int saleType);

    boolean noStock();

    boolean purchase(int customerId, int productId);

    boolean checkLoyaltyCardEligibility(int id);

    boolean grantLoyalty(int id);

    void printLastNPurchases(int n);

    Product getProduct(int id);

    @Nullable ResultSet query(@NotNull String sql);

    @Nullable String retrieveProductName(int id);

    @NotNull List<Integer> stockMonitor();

    @NotNull Map<String, Object> generateReport();

}
