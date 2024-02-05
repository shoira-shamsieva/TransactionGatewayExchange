# Purchase Transaction API

This API enables users to store and retrieve purchase transactions with currency conversion capabilities.

## Store a Purchase Transaction

Store a purchase transaction with a description, transaction date, and purchase amount in USD. Each transaction is assigned a unique identifier.

- **Endpoint:** `POST /api/purchases`
- **Request Payload Example:**
    ```json
    {
      "description": "Sample Purchase",
      "transactionDate": "2024-02-05",
      "amount": 100.50
    }
    ```
- **Response Example:**
    ```json
    {
      "uniqueIdentifier": "550e8400-e29b-41d4-a716-446655440000"
    }
    ```

## Retrieve a Purchase Transaction in a Specified Currency

Retrieve a stored purchase transaction converted to a specified currency using the Treasury Reporting Rates of Exchange API.

- **Endpoint:** `GET /api/purchases/{uniqueIdentifier}?currency=USD`
- **Response Example:**
    ```json
    {
      "description": "Sample Purchase",
      "transactionDate": "2024-02-05",
      "originalPurchaseAmount": 100.50,
      "exchangeRateUsed": 1.0,
      "convertedAmount": 100.50
    }
    ```

## Requirements

- **Description:** Must not exceed 50 characters.
- **Transaction date:** Must be a valid date format.
- **Purchase amount:** Must be a valid positive amount rounded to the nearest cent.
- **Unique identifier:** Must uniquely identify the purchase.

## Currency Conversion

- Use a currency conversion rate within the last 6 months.
- If no rate is available within 6 months, return an error.
- Converted amount rounded to two decimal places.

## Treasury Reporting Rates of Exchange API

The application utilizes the Treasury Reporting Rates of Exchange API for currency conversion. API documentation is available [here](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange).

## API Deployment

1. **Store a Purchase Transaction:**
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"description": "Sample Purchase", "transactionDate": "2024-02-05", "amount": 100.50}' http://localhost:8080/api/purchase
   ```

2. **Retrieve a Purchase Transaction in USD:**
   ```bash
   curl http://localhost:8080/api/purchases/{uniqueIdentifier}?currency=USD
   ```

Replace `{uniqueIdentifier}` with the actual identifier returned after storing a purchase.
