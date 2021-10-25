package uk.ac.napier.sa.controller;

import uk.ac.napier.sa.controller.adt.Product;
import uk.ac.napier.sa.model.RemoteDatabaseManager;

public class Controller {

    private final RemoteDatabaseManager rdbm;

    public Controller(RemoteDatabaseManager rdbm) {
        this.rdbm = rdbm;
    }

    /**
     * Obtain relevant product information.
     * @param id
     * @return
     */
    public String retrieveProduct(int id) {
        Product p = rdbm.getProduct(id);
        return p.toString();
    }


}
