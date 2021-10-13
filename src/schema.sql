-- --------------------
--  Database Schema  --
-- --------------------
--
DROP TABLE IF EXISTS `transaction`;
DROP TABLE IF EXISTS `sale`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `customer`;
--
-- --------------------
--   Structure for   --
--  table `customer` --
-- --------------------
CREATE TABLE IF NOT EXISTS `customer` (id    int          NOT NULL AUTO_INCREMENT PRIMARY KEY, name  varchar(255) NOT NULL, loyal tinyint(1)   NOT NULL DEFAULT 0);
--
-- --------------------
--   Structure for   --
--  table `product` --
-- --------------------
CREATE TABLE IF NOT EXISTS `product` (id    int            NOT NULL AUTO_INCREMENT PRIMARY KEY, name  varchar(255)   NOT NULL, stock int            NOT NULL, price decimal(16, 2) NOT NULL);
--
-- --------------------
--   Structure for   --
--    table `sale`   --
-- --------------------
CREATE TABLE IF NOT EXISTS `sale` (id      int NOT NULL AUTO_INCREMENT PRIMARY KEY, product int NOT NULL, type    int NOT NULL, CONSTRAINT product FOREIGN KEY product (product) REFERENCES product (id));
--
-- --------------------
--   Structure for   --
--       table       --
--   `transaction`  --
-- --------------------
CREATE TABLE IF NOT EXISTS `transaction`(id        int            NOT NULL AUTO_INCREMENT PRIMARY KEY, customer  int            NOT NULL, sale      int            NOT NULL, cost      decimal(16, 2) NOT NULL, purchased TIMESTAMP DEFAULT CURRENT_TIMESTAMP, CONSTRAINT customer FOREIGN KEY customer (customer) REFERENCES customer (id), CONSTRAINT sale FOREIGN KEY sale (sale) REFERENCES sale(id));