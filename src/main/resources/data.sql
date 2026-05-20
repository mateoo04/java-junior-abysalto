INSERT INTO buyer (first_name, last_name, title) VALUES ('Ivan', 'Horvat', NULL);
INSERT INTO buyer (first_name, last_name, title) VALUES ('Ana', 'Kovačević', NULL);
INSERT INTO buyer (first_name, last_name, title) VALUES ('Luka', 'Babić', NULL);
INSERT INTO buyer (first_name, last_name, title) VALUES ('Petra', 'Novak', NULL);
INSERT INTO buyer (first_name, last_name, title) VALUES ('Marko', 'Jurić', NULL);

INSERT INTO buyer_address (city, street, home_number) VALUES ('Zagreb', 'Ilica', '15');
INSERT INTO buyer_address (city, street, home_number) VALUES ('Zagreb', 'Slavonska avenija', '56');
INSERT INTO buyer_address (city, street, home_number) VALUES ('Zagreb', 'Prisavlje', '8');

INSERT INTO "order" (
	buyer_id,
	order_status,
	order_time,
	payment_option,
	delivery_address_id,
	contact_number,
	note,
	currency,
	total_price
) VALUES (
	1,
	'WAITING_FOR_CONFIRMATION',
	CURRENT_TIMESTAMP,
	'CASH',
	1,
	'+385911111111',
	'Bez luka',
	'EUR',
	15.20
);

INSERT INTO "order" (
	buyer_id,
	order_status,
	order_time,
	payment_option,
	delivery_address_id,
	contact_number,
	note,
	currency,
	total_price
) VALUES (
	2,
	'PREPARING',
	CURRENT_TIMESTAMP,
	'CARD_UPFRONT',
	2,
	'+385922222222',
	'Dostaviti na portu',
	'EUR',
	23.40
);

INSERT INTO order_item (order_nr, item_nr, name, quantity, price) VALUES (1, 1, 'Rižoto s piletinom', 1, 11.20);
INSERT INTO order_item (order_nr, item_nr, name, quantity, price) VALUES (1, 2, 'Sezonska salata', 1, 4.00);
INSERT INTO order_item (order_nr, item_nr, name, quantity, price) VALUES (2, 1, 'Pureći medaljoni s njokima', 1, 16.90);
INSERT INTO order_item (order_nr, item_nr, name, quantity, price) VALUES (2, 2, 'Juha od rajčice', 1, 3.50);
INSERT INTO order_item (order_nr, item_nr, name, quantity, price) VALUES (2, 3, 'Cijeđena naranča', 1, 3.00);
