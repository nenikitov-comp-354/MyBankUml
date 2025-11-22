-- Banks Data
INSERT INTO bank (name) VALUES
('My Bank');

-- Branches Data
INSERT INTO branches (address, bank_id) VALUES
('123 Main St, Montreal, CA', 1),
('456 Elm St, Montreal, CA', 1),
('100 King St W, Toronto, ON', 1);

-- Customers Data
INSERT INTO customer (first_name, last_name, date_of_birth, social_insurance_number, phone, email, branch_id) VALUES
('Harry', 'Styles', '1994-02-01', '123-456-789', '+14165551234', 'harry.styles@example.com', 1),
('Sabrina', 'Carpenter', '1999-05-11', '234-567-890', '+16045557890', 'sabrina.carpenter@example.com', 2),
('Carey', 'Price', '1987-08-16', '345-678-901', '+12045553456', 'carey.price@example.com', 3),
('Zara', 'Larsson', '1997-12-16', '456-789-012', '+17785559876', 'zara.larsson@example.com', 1),
('Tate', 'McRae', '2003-07-01', '567-890-123', '+15875551212', 'tate.mcrae@example.com', 2),
('Sidney', 'Crosby', '1987-08-07', '678-901-234', '+19025550123', 'sidney.crosby@example.com', 3),
('Connor', 'McDavid', '1997-01-13', '901-234-567', '+17805551234', 'connor.mcdavid@example.com', 1);

-- Customer Logins Data
INSERT INTO customer_login (customer_id, password) VALUES
(1, 'harry_pswd'),
(2, 'sabrina_pswd'),
(3, 'carey_pswd'),
(4, 'zara_pswd'),
(5, 'tate_pswd'),
(6, 'sidney_pswd'),
(7, 'connor_pswd');

-- Accounts and Transactions
WITH
-- Admin account
admin AS (
    INSERT INTO account (name, customer_id, is_locked)
    VALUES ('Admin Account', NULL, true)
    RETURNING id AS admin_id
),
-- Harry Styles accounts
harry_checking AS (
    INSERT INTO account_checking (name, customer_id, monthly_fee)
    VALUES ('Harry Checking', 1, 3.50)
    RETURNING id AS hc_id
),
harry_savings AS (
    INSERT INTO account_savings (name, customer_id, interest_rate)
    VALUES ('Harry Savings', 1, 0.0150)
    RETURNING id AS hs_id
),
harry_credit AS (
    INSERT INTO account_credit (name, customer_id, credit_limit, payment_grace_days)
    VALUES ('Harry Credit', 1, 5000.00, 21)
    RETURNING id AS hcr_id
),
-- Sabrina Carpenter accounts
sabrina_checking AS (
    INSERT INTO account_checking (name, customer_id, monthly_fee)
    VALUES ('Sabrina Checking', 2, 3.50)
    RETURNING id AS sc_id
),
sabrina_savings AS (
    INSERT INTO account_savings (name, customer_id, interest_rate)
    VALUES ('Sabrina Savings', 2, 0.0150)
    RETURNING id AS ss_id
),
sabrina_credit AS (
    INSERT INTO account_credit (name, customer_id, credit_limit, payment_grace_days)
    VALUES ('Sabrina Credit', 2, 4000.00, 21)
    RETURNING id AS scr_id
),
-- Carey Price accounts
carey_checking AS (
    INSERT INTO account_checking (name, customer_id, monthly_fee)
    VALUES ('Carey Checking', 3, 3.50)
    RETURNING id AS cc_id
),
carey_savings AS (
    INSERT INTO account_savings (name, customer_id, interest_rate)
    VALUES ('Carey Savings', 3, 0.0150)
    RETURNING id AS cs_id
),
carey_credit AS (
    INSERT INTO account_credit (name, customer_id, credit_limit, payment_grace_days)
    VALUES ('Carey Credit', 3, 3000.00, 21)
    RETURNING id AS ccr_id
),
-- Zara Larsson accounts
zara_checking AS (
    INSERT INTO account_checking (name, customer_id, monthly_fee)
    VALUES ('Zara Checking', 4, 3.50)
    RETURNING id AS zc_id
),
zara_savings AS (
    INSERT INTO account_savings (name, customer_id, interest_rate)
    VALUES ('Zara Savings', 4, 0.0150)
    RETURNING id AS zs_id
),
zara_credit AS (
    INSERT INTO account_credit (name, customer_id, credit_limit, payment_grace_days)
    VALUES ('Zara Credit', 4, 3500.00, 21)
    RETURNING id AS zcr_id
),
-- Tate McRae accounts
tate_checking AS (
    INSERT INTO account_checking (name, customer_id, monthly_fee)
    VALUES ('Tate Checking', 5, 3.50)
    RETURNING id AS tc_id
),
tate_savings AS (
    INSERT INTO account_savings (name, customer_id, interest_rate)
    VALUES ('Tate Savings', 5, 0.0150)
    RETURNING id AS ts_id
),
tate_credit AS (
    INSERT INTO account_credit (name, customer_id, credit_limit, payment_grace_days)
    VALUES ('Tate Credit', 5, 2500.00, 21)
    RETURNING id AS tcr_id
),
-- Sidney Crosby accounts
sidney_checking AS (
    INSERT INTO account_checking (name, customer_id, monthly_fee)
    VALUES ('Sidney Checking', 6, 3.50)
    RETURNING id AS sic_id
),
sidney_savings AS (
    INSERT INTO account_savings (name, customer_id, interest_rate)
    VALUES ('Sidney Savings', 6, 0.0150)
    RETURNING id AS sis_id
),
sidney_credit AS (
    INSERT INTO account_credit (name, customer_id, credit_limit, payment_grace_days)
    VALUES ('Sidney Credit', 6, 4500.00, 21)
    RETURNING id AS scc_id
),
-- Connor McDavid accounts
connor_checking AS (
    INSERT INTO account_checking (name, customer_id, monthly_fee)
    VALUES ('Connor Checking', 7, 3.50)
    RETURNING id AS coc_id
),
connor_savings AS (
    INSERT INTO account_savings (name, customer_id, interest_rate)
    VALUES ('Connor Savings', 7, 0.0150)
    RETURNING id AS cos_id
),
connor_credit AS (
    INSERT INTO account_credit (name, customer_id, credit_limit, payment_grace_days)
    VALUES ('Connor Credit', 7, 5000.00, 21)
    RETURNING id AS ccr2_id
)

-- Insert initial deposits
INSERT INTO transaction (account_id_source, account_id_destination, amount, time)
SELECT a.admin_id, hc.hc_id, 1200.00, now() FROM admin a, harry_checking hc
UNION ALL
SELECT a.admin_id, hs.hs_id, 5000.00, now() FROM admin a, harry_savings hs
UNION ALL
SELECT a.admin_id, hcr.hcr_id, 3000.00, now() FROM admin a, harry_credit hcr
UNION ALL
SELECT a.admin_id, sc.sc_id, 1100.00, now() FROM admin a, sabrina_checking sc
UNION ALL
SELECT a.admin_id, ss.ss_id, 4000.00, now() FROM admin a, sabrina_savings ss
UNION ALL
SELECT a.admin_id, scr.scr_id, 2500.00, now() FROM admin a, sabrina_credit scr
UNION ALL
SELECT a.admin_id, cc.cc_id, 900.00, now() FROM admin a, carey_checking cc
UNION ALL
SELECT a.admin_id, cs.cs_id, 3500.00, now() FROM admin a, carey_savings cs
UNION ALL
SELECT a.admin_id, ccr.ccr_id, 2000.00, now() FROM admin a, carey_credit ccr
UNION ALL
SELECT a.admin_id, zc.zc_id, 1000.00, now() FROM admin a, zara_checking zc
UNION ALL
SELECT a.admin_id, zs.zs_id, 3000.00, now() FROM admin a, zara_savings zs
UNION ALL
SELECT a.admin_id, zcr.zcr_id, 1800.00, now() FROM admin a, zara_credit zcr
UNION ALL
SELECT a.admin_id, tc.tc_id, 950.00, now() FROM admin a, tate_checking tc
UNION ALL
SELECT a.admin_id, ts.ts_id, 3200.00, now() FROM admin a, tate_savings ts
UNION ALL
SELECT a.admin_id, tcr.tcr_id, 1500.00, now() FROM admin a, tate_credit tcr
UNION ALL
SELECT a.admin_id, sic.sic_id, 1200.00, now() FROM admin a, sidney_checking sic
UNION ALL
SELECT a.admin_id, sis.sis_id, 2800.00, now() FROM admin a, sidney_savings sis
UNION ALL
SELECT a.admin_id, scc.scc_id, 2200.00, now() FROM admin a, sidney_credit scc
UNION ALL
SELECT a.admin_id, coc.coc_id, 1100.00, now() FROM admin a, connor_checking coc
UNION ALL
SELECT a.admin_id, cos.cos_id, 3000.00, now() FROM admin a, connor_savings cos
UNION ALL
SELECT a.admin_id, ccr2.ccr2_id, 2500.00, now() FROM admin a, connor_credit ccr2;

-- Additional Transactions between customers
INSERT INTO transaction (account_id_source, account_id_destination, amount, time)
SELECT hc.hc_id, sc.scr_id, 75.00, now() FROM harry_checking hc, sabrina_credit sc
UNION ALL
SELECT hc.hc_id, cc.ccr_id, 50.00, now() FROM harry_checking hc, carey_credit cc
UNION ALL
SELECT sic.sic_id, coc.ccr2_id, 100.00, now() FROM sidney_checking sic, connor_credit coc;