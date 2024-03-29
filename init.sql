-- init.sql
CREATE DATABASE IF NOT EXISTS TXN_GATEWAY;
USE TXN_GATEWAY;

CREATE TABLE IF NOT EXISTS PURCHASES (
    unique_identifier UUID PRIMARY KEY,
    description VARCHAR(50) NOT NULL,
    transaction_date DATE NOT NULL,
    amount DECIMAL(10,2) CHECK (amount >= 0) NOT NULL,
    insertion_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);