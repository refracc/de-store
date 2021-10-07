-- --------------------
--  Database Schema  --
-- --------------------

DROP TABLE IF EXISTS `transaction`;
DROP TABLE IF EXISTS `sale`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `customer`;

-- --------------------
--   Structure for   --
--  table `customer` --
-- --------------------
CREATE TABLE IF NOT EXISTS `customer`
(
    id    INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name  varchar(255) NOT NULL,
    loyal boolean      NOT NULL DEFAULT false
);

-- --------------------
--   Structure for   --
--  table `product` --
-- --------------------
CREATE TABLE IF NOT EXISTS `product`
(
    id    INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name  varchar(255)   NOT NULL,
    stock INT            NOT NULL,
    price DECIMAL(16, 2) NOT NULL
);

-- --------------------
--   Structure for   --
--    table `sale`   --
-- --------------------
CREATE TABLE IF NOT EXISTS `sale`
(
    id      INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product INT NOT NULL,
    type    INT NOT NULL,

    CONSTRAINT product
        FOREIGN KEY product (product) REFERENCES product (id)
);

-- --------------------
--   Structure for   --
--       table       --
--   `transaction`  --
-- --------------------
CREATE TABLE IF NOT EXISTS `transaction`
(
    id        INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product   INT            NOT NULL,
    customer  INT            NOT NULL,
    sale      INT            NOT NULL,
    cost      DECIMAL(16, 2) NOT NULL,
    purchased TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT product_id
        FOREIGN KEY product_id (product) REFERENCES product (id),

    CONSTRAINT customer
        FOREIGN KEY customer (customer) REFERENCES customer (id),

    CONSTRAINT sale
        FOREIGN KEY sale (sale) REFERENCES sale(id)
);