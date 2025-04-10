## Crypto Trade Platform - Backend Documentation

## Getting Started

## Prerequisites

Apache Maven 3.9.9

Java JDK 17

MySQL Server

## Database Setup

Create a MySQL database named crypto_trading_platform and the required tables by using the sql script provided in the repo:

Update database connection details in DatabaseConfig.java if needed.

## Running the Application

From the backend directory

Create a libs folder inside the backend directory

Install the dependencies using a terminal:
### `mvn dependency:copy-dependencies -DoutputDirectory=your_directory`
Replace your_directory with the directory example: C:\crypto-trade-platform\backend\libs 


Compile the code using a terminal:
### `javac -d . -cp "libs/*" src/*.java`


Run the application using a terminal:
### `java -cp ".;libs/*" com.crypto.platform.Main`

(Use : instead of ; on Mac/Linux)


## Application Overview
The Crypto Trade Platform backend is a Spring Boot application that provides a trading simulation environment for cryptocurrency trading.

## Key Components

WebSocket Client: Connects to Kraken's WebSocket API to receive real-time cryptocurrency price data.

REST API: Exposes endpoints for the frontend to interact with user data, trading functionality, and market data.

Database Layer: Manages user balances, cryptocurrency holdings, and transaction history.

## Main Classes
CryptoTradingApplication: Entry point that initializes components and connects to the Kraken WebSocket.

KrakenWebSocketClient: Handles the WebSocket connection and parses incoming market data.

TickerService: Processes and stores cryptocurrency price data.

UserController: REST endpoint handlers for user operations (buy, sell, portfolio management).

DAO Classes: Data Access Objects for database operations:

UserDAO: Manages user accounts

HoldingDAO: Tracks user cryptocurrency holdings

TransactionDAO: Records trading transactions

## API Endpoints

GET /api/user: Retrieves user balance and holdings

GET /api/tickers: Lists all available cryptocurrencies with current prices

GET /api/transactions: Gets user's transaction history

POST /api/buy: Executes a buy order

POST /api/sell: Executes a sell order

POST /api/reset: Resets user account to initial state

## Data Flow

Market data flows from Kraken's API to the application via WebSocket

The application processes and stores this data in memory

User actions (buy/sell) are validated and executed against this market data

Transactions are recorded and user balances/holdings are updated


The frontend receives all data through REST API calls

The backend implements FIFO (First In, First Out) accounting for calculating profit/loss when selling cryptocurrencies.
