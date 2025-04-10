## Crypto Trade Platform - Frontend Documentation

## Getting Started

## Prerequisites

Node.js (v16 or higher)

npm (comes with Node.js)

## Installation

Navigate to the frontend directory:
### `cd ...\crypto-trade-platform\frontend`

Install dependencies:
### `npm install`

Start the development server:
### `npm start`

The application will automatically open in your browser at http://localhost:3000.

## Application Overview
The Crypto Trade Platform is a simulated cryptocurrency trading application that allows users to buy and sell various cryptocurrencies using virtual money.

## Key Features
Real-time Price Data: Displays current prices and market data for 20 cryptocurrencies, sorted by trading volume.

Trading Functionality: Users can buy and sell cryptocurrencies with a simple modal interface.

Portfolio Management: Shows users' current holdings and their value in USD.

Transaction History: Records all buy/sell transactions with profit/loss calculations for sell transactions.

Account Reset: Allows users to reset their account back to the initial $10,000 balance.

## Main Components
Home Page: The main dashboard showing cryptocurrency prices, user balance, and portfolio.

Header: Simple navigation header with the application title.

Trade Modal: Interactive dialog for executing buy/sell transactions.

Transaction History: Modal showing all past transactions with details.

## Data Flow
The application fetches cryptocurrency data from the backend API every 5 seconds to display real-time prices.

User data (balance and holdings) is fetched on initial load and after each transaction.

Trade actions are sent to the backend, which processes the transaction and updates the database.

Profit/loss is calculated based on the purchase history using a FIFO (First In, First Out) method.

## Technologies Used
React: Front-end library for building the user interface

Tailwind CSS: Utility-first CSS framework for styling

React Router: For navigation (though currently only a single page is used)

Fetch API: For making HTTP requests to the backend

The application is designed to provide a realistic trading experience with market data coming from the Kraken cryptocurrency exchange via the backend WebSocket connection.
