package uk.ac.napier.sa.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.ac.napier.sa.controller.adt.Product;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public sealed interface RemoteDatabaseManager permits DatabaseManager {


    default boolean connect(@NotNull String host, int port, @NotNull String database, boolean useSSL, @NotNull String user, @NotNull String pass) {
        return false;
    }

    default boolean disconnect() {
        return false;
    }

    default boolean init(String path) {
        return false;
    }

    default boolean changePrice(int id, double price) {
        return false;
    }

    default boolean sell(int id, int saleType) {
        return false;
    }

    default boolean noStock() {
        return false;
    }

    default boolean purchase(int customerId, int productId) {
        return false;
    }

    default boolean checkLoyaltyCardEligibility(int id) {
        return false;
    }

    default boolean grantLoyalty(int id) {
        return false;
    }

    void printLastNPurchases(int n);

    default Product getProduct(int id) {
        return null;
    }

    @Nullable
    default ResultSet query(@NotNull String sql) {
        return null;
    }

    @Nullable
    default String retrieveProductName(int id) {
        return "";
    }

    @NotNull
    default List<Integer> stockMonitor() {
        return Collections.emptyList();
    }

    @NotNull
    default Map<String, Object> generateReport() {
        return Collections.emptyMap();
    }

}
