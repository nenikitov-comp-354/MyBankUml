-- Sample bank
INSERT INTO bank (name) VALUES ('Team 4 Bank');

-- Sample branch linked to the bank
INSERT INTO branch (address, bank_id)
VALUES ('123 Main Street, Montreal, QC', 1);

-- Sample customers with branch_id = 1
INSERT INTO customer (
    first_name, last_name, date_of_birth, social_insurance_number, phone, email, branch_id
) VALUES
('Harry',  'Styles',     DATE '1994-02-01', '123-456-789', '+14165551234', 'harry.styles@example.com', 1),
('Sabrina','Carpenter',  DATE '1999-05-11', '234-567-890', '+16045557890', 'sabrina.carpenter@example.com', 1),
('Carey',  'Price',      DATE '1987-08-16', '345-678-901', '+12045553456', 'carey.price@example.com', 1),
('Zara',   'Larsson',    DATE '1997-12-16', '456-789-012', '+17785559876', 'zara.larsson@example.com', 1),
('Tate',   'McRae',      DATE '2003-07-01', '567-890-123', '+15875551212', 'tate.mcrae@example.com', 1),
('Sidney', 'Crosby',     DATE '1987-08-07', '678-901-234', '+19025550123', 'sidney.crosby@example.com', 1),
('Connor', 'McDavid',    DATE '1997-01-13', '901-234-567', '+17805551234', 'connor.mcdavid@example.com', 1);

-- Inserting sample accounts for each customer

-- HARRY STYLES (Customer ID 1)
WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Harry Checking', false, 1)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_checking (id, name, is_locked, customer_id, monthly_fee)
SELECT id, name, is_locked, customer_id, 4.99 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Harry Savings', false, 1)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_savings (id, name, is_locked, customer_id, interest_rate)
SELECT id, name, is_locked, customer_id, 1.25 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Harry Credit', false, 1)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_credit (id, name, is_locked, customer_id, credit_limit, payment_grace_days)
SELECT id, name, is_locked, customer_id, 5000.00, 21 FROM acc;

-- SABRINA CARPENTER (Customer ID 2)
WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Sabrina Checking', false, 2)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_checking (id, name, is_locked, customer_id, monthly_fee)
SELECT id, name, is_locked, customer_id, 3.50 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Sabrina Savings', false, 2)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_savings (id, name, is_locked, customer_id, interest_rate)
SELECT id, name, is_locked, customer_id, 1.50 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Sabrina Credit', false, 2)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_credit (id, name, is_locked, customer_id, credit_limit, payment_grace_days)
SELECT id, name, is_locked, customer_id, 3000.00, 25 FROM acc;

-- CAREY PRICE (Customer ID 3)
WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Carey Checking', false, 3)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_checking (id, name, is_locked, customer_id, monthly_fee)
SELECT id, name, is_locked, customer_id, 5.00 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Carey Savings', false, 3)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_savings (id, name, is_locked, customer_id, interest_rate)
SELECT id, name, is_locked, customer_id, 1.75 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Carey Credit', false, 3)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_credit (id, name, is_locked, customer_id, credit_limit, payment_grace_days)
SELECT id, name, is_locked, customer_id, 7000.00, 20 FROM acc;

-- ZARA LARSSON (Customer ID 4)
WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Zara Checking', false, 4)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_checking (id, name, is_locked, customer_id, monthly_fee)
SELECT id, name, is_locked, customer_id, 2.75 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Zara Savings', false, 4)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_savings (id, name, is_locked, customer_id, interest_rate)
SELECT id, name, is_locked, customer_id, 1.20 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Zara Credit', false, 4)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_credit (id, name, is_locked, customer_id, credit_limit, payment_grace_days)
SELECT id, name, is_locked, customer_id, 4000.00, 22 FROM acc;

-- TATE MCRAE (Customer ID 5)
WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Tate Checking', false, 5)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_checking (id, name, is_locked, customer_id, monthly_fee)
SELECT id, name, is_locked, customer_id, 3.99 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Tate Savings', false, 5)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_savings (id, name, is_locked, customer_id, interest_rate)
SELECT id, name, is_locked, customer_id, 1.80 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Tate Credit', false, 5)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_credit (id, name, is_locked, customer_id, credit_limit, payment_grace_days)
SELECT id, name, is_locked, customer_id, 2500.00, 21 FROM acc;

-- SIDNEY CROSBY (Customer ID 6)
WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Sidney Checking', false, 6)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_checking (id, name, is_locked, customer_id, monthly_fee)
SELECT id, name, is_locked, customer_id, 4.25 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Sidney Savings', false, 6)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_savings (id, name, is_locked, customer_id, interest_rate)
SELECT id, name, is_locked, customer_id, 1.60 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Sidney Credit', false, 6)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_credit (id, name, is_locked, customer_id, credit_limit, payment_grace_days)
SELECT id, name, is_locked, customer_id, 8000.00, 30 FROM acc;

-- CONNOR MCDAVID (Customer ID 7)
WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Connor Checking', false, 7)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_checking (id, name, is_locked, customer_id, monthly_fee)
SELECT id, name, is_locked, customer_id, 3.00 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Connor Savings', false, 7)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_savings (id, name, is_locked, customer_id, interest_rate)
SELECT id, name, is_locked, customer_id, 1.30 FROM acc;

WITH acc AS (
  INSERT INTO account (name, is_locked, customer_id)
  VALUES ('Connor Credit', false, 7)
  RETURNING id, name, is_locked, customer_id
)
INSERT INTO account_credit (id, name, is_locked, customer_id, credit_limit, payment_grace_days)
SELECT id, name, is_locked, customer_id, 6000.00, 18 FROM acc;

-- Sample transactions between accounts

-- Harry sends $200 to Sabrina
INSERT INTO transaction (account_id_source, account_id_destination, amount, time)
VALUES (1, 2, 200.00, CURRENT_TIMESTAMP);

-- Carey pays Zara $150
INSERT INTO transaction (account_id_source, account_id_destination, amount, time)
VALUES (3, 4, 150.00, CURRENT_TIMESTAMP);

-- Tate transfers $75 to Sidney
INSERT INTO transaction (account_id_source, account_id_destination, amount, time)
VALUES (5, 6, 75.00, CURRENT_TIMESTAMP);

-- Connor pays Carey $50
INSERT INTO transaction (account_id_source, account_id_destination, amount, time)
VALUES (7, 3, 50.00, CURRENT_TIMESTAMP);

-- Zara sends $100 to Harry
INSERT INTO transaction (account_id_source, account_id_destination, amount, time)
VALUES (4, 1, 100.00, CURRENT_TIMESTAMP);