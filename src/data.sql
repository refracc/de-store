-- ----------------------- --
--      Customer Data      --
-- ----------------------- --
INSERT INTO customer (`name`, `loyal`) VALUES ('Zarina Landau', 1);
INSERT INTO customer (`name`, `loyal`) VALUES ('Charlotte Antonis', 0);
INSERT INTO customer (`name`, `loyal`) VALUES ('Raiden Tirrell', 0);
INSERT INTO customer (`name`, `loyal`) VALUES ('Sammie Seabrooke', 1);
INSERT INTO customer (`name`, `loyal`) VALUES ('Simon Kingsley', 0);
INSERT INTO customer (`name`, `loyal`) VALUES ('Chantelle Reynolds', 1);
INSERT INTO customer (`name`, `loyal`) VALUES ('Alycia Kendal', 0);
INSERT INTO customer (`name`, `loyal`) VALUES ('Pierce Boothman', 0);
INSERT INTO customer (`name`, `loyal`) VALUES ('Ileen Banner', 1);
INSERT INTO customer (`name`, `loyal`) VALUES ('Rickey Clayton', 0);
INSERT INTO customer (`name`, `loyal`) VALUES ('Cal Skeates', 1);
--
-- ----------------------- --
--       Product Data      --
-- ----------------------- --
--
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Red Wine', 384, 11.50);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('White Wine', 768, 14.80);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Rosé Wine', 474, 24.60);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Vodka', 54, 9.70);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Gin', 1, 10.50);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Spiced Rum', 2, 16.50);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Bacardi', 5, 14.30);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Whisky', 24, 35.00);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Whiskey', 16, 17.00);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Tequila', 4, 16.60);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Bottled Lager', 816, 5.60);
INSERT INTO product (`name`, `stock`, `price`) VALUES ('Bottled Beer', 288, 4.80);
--
-- ----------------------- --
--        Sale Data        --
-- ----------------------- --
INSERT INTO sale (`product`, `type`) VALUES (4, 1);
INSERT INTO sale (`product`, `type`) VALUES (3, 1);
INSERT INTO sale (`product`, `type`) VALUES (9, 1);
INSERT INTO sale (`product`, `type`) VALUES (3, 3);
INSERT INTO sale (`product`, `type`) VALUES (11, 2);
INSERT INTO sale (`product`, `type`) VALUES (1, 2);
INSERT INTO sale (`product`, `type`) VALUES (4, 1);
INSERT INTO sale (`product`, `type`) VALUES (7, 2);
INSERT INTO sale (`product`, `type`) VALUES (10, 3);
INSERT INTO sale (`product`, `type`) VALUES (6, 1);
INSERT INTO sale (`product`, `type`) VALUES (8, 3);
--
-- ----------------------- --
--    Transaction Data     --
-- ----------------------- --
--
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (1, 1, 17.46, '2021-08-10 12:23:56');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (4, 4, 369.00, '2021-08-25 15:45:12');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (3, 5, 134.40, '2021-09-04 09:45:40');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (5, 2, 49.20, '2021-09-09 12:00:14');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (8, 3, 57.50, '2021-09-13 16:34:40');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (9, 3, 70.00, '2021-09-28 13:53:01');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (2, 10, 66.00, '2021-10-02 12:00:12');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (1, 10, 59.40, '2021-10-02 12:01:34');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (5, 10, 148.50, '2021-10-02 12:04:23');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (11, 10, 14.85, '2021-10-02 12:05:15');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (7, 10, 16.50, '2021-10-02 12:10:19');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (9, 8, 25.74, '2021-10-10 14:04:21');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (6, 11, 31.50, '2021-10-11 10:10:45');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (1, 11, 31.50, '2021-10-11 10:15:34');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (7, 11, 35.00, '2021-10-11 11:24:19');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (3, 11, 35.00, '2021-10-12 15:11:19');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (9, 11, 31.50, '2021-10-12 18:10:56');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (10, 11, 35.00, '2021-10-12 18:13:38');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (4, 11, 31.50, '2021-10-13 10:00:43');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (8, 11, 35.00, '2021-10-13 19:18:13');
INSERT INTO transaction (`customer`, `sale`, `cost`, `purchased`) VALUES (6, 11, 31.5, '2021-10-13 23:56:01');