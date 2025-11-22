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
CREATE OR REPLACE FUNCTION create_customer_accounts(
    customer_id INTEGER,
    base_name TEXT,
    checking_fee NUMERIC,
    savings_rate NUMERIC,
    credit_limit NUMERIC,
    grace_days INTEGER
) RETURNS VOID AS $$
BEGIN
  -- Checking
  WITH acc AS (
    INSERT INTO account (name, is_locked, customer_id)
    VALUES (base_name || ' Checking', false, customer_id)
    RETURNING id, name, is_locked
  )
  INSERT INTO account_checking (id, name, is_locked, customer_id, monthly_fee)
  SELECT acc.id, acc.name, acc.is_locked, c.customer_id, checking_fee
  FROM acc CROSS JOIN (SELECT customer_id) AS c;

  -- Savings
  WITH acc AS (
    INSERT INTO account (name, is_locked, customer_id)
    VALUES (base_name || ' Savings', false, customer_id)
    RETURNING id, name, is_locked
  )
  INSERT INTO account_savings (id, name, is_locked, customer_id, interest_rate)
  SELECT acc.id, acc.name, acc.is_locked, c.customer_id, savings_rate
  FROM acc CROSS JOIN (SELECT customer_id) AS c;

  -- Credit
  WITH acc AS (
    INSERT INTO account (name, is_locked, customer_id)
    VALUES (base_name || ' Credit', false, customer_id)
    RETURNING id, name, is_locked
  )
  INSERT INTO account_credit (id, name, is_locked, customer_id, credit_limit, payment_grace_days)
  SELECT acc.id, acc.name, acc.is_locked, c.customer_id, credit_limit, grace_days
  FROM acc CROSS JOIN (SELECT customer_id) AS c;
END;
$$ LANGUAGE plpgsql;

SELECT create_customer_accounts(1, 'Harry',   4.99, 1.25, 5000.00, 21);
SELECT create_customer_accounts(2, 'Sabrina', 3.50, 1.50, 3000.00, 25);
SELECT create_customer_accounts(3, 'Carey',   5.00, 1.75, 7000.00, 20);
SELECT create_customer_accounts(4, 'Zara',    2.75, 1.20, 4000.00, 22);
SELECT create_customer_accounts(5, 'Tate',    3.99, 1.80, 2500.00, 21);
SELECT create_customer_accounts(6, 'Sidney',  4.25, 1.60, 8000.00, 30);
SELECT create_customer_accounts(7, 'Connor',  3.00, 1.30, 6000.00, 18);

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