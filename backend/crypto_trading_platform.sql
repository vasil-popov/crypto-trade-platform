CREATE DATABASE crypto_trading_platform;

use crypto_trading_platform;

CREATE TABLE users (
id INT AUTO_INCREMENT PRIMARY KEY,
balance DECIMAL(18, 2) NOT NULL DEFAULT 10000.00,
initial_balance DECIMAL(18, 2) NOT NULL DEFAULT 10000.00
);

CREATE TABLE holdings (
id INT AUTO_INCREMENT PRIMARY KEY,
user_id INT,
symbol VARCHAR(10),
quantity DECIMAL(18, 8) NOT NULL DEFAULT 0,
FOREIGN KEY (user_id) REFERENCES users(id),
UNIQUE (user_id, symbol)
);

CREATE TABLE transactions (
id INT AUTO_INCREMENT PRIMARY KEY,
user_id INT,
symbol VARCHAR(10),
quantity DECIMAL(18, 8) NOT NULL,
price DECIMAL(18, 2) NOT NULL,
total_amount DECIMAL(18, 2) NOT NULL,
action ENUM('BUY', 'SELL') NOT NULL,
timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (user_id) REFERENCES users(id)
);

use crypto_trading_platform;
SELECT * FROM users;
SELECT * FROM holdings;
SELECT * FROM transactions;
