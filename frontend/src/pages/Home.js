import React, { useEffect, useState, useCallback } from "react";
import Header from "../components/Header";
import TradeModal from "../components/TradeModal";

const Home = () => {
  const [tickers, setTickers] = useState([]);
  const [selectedTicker, setSelectedTicker] = useState(null);
  const [quantity, setQuantity] = useState("");
  const [error, setError] = useState("");
  const [showHoldings, setShowHoldings] = useState(false);
  const [showHistory, setShowHistory] = useState(false);
  const [transactions, setTransactions] = useState([]);
  const [balance, setBalance] = useState(10000);
  const [holdings, setHoldings] = useState({});

  const fetchUserData = useCallback(async () => {
    try {
      const response = await fetch("http://localhost:8080/api/user");
      const data = await response.json();
      
      setBalance(data.balance);
      setHoldings(data.holdings || {});
    } catch (error) {
      console.error("Error fetching user data:", error);
    }
  }, []);

  const fetchTransactions = useCallback(async () => {
    try {
      const response = await fetch("http://localhost:8080/api/transactions");
      const data = await response.json();
      
      const formattedTransactions = data.map(tx => ({
        ...tx,
        timestamp: new Date(tx.timestamp).toLocaleString(),
      }));
      
      setTransactions(formattedTransactions);
    } catch (error) {
      console.error("Error fetching transactions:", error);
    }
  }, []);

  useEffect(() => {
    const fetchTickers = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/tickers");
        const data = await response.json();

        const mappedData = data.map((ticker) => ({
          ...ticker,
          quantityOwned: holdings[ticker.symbol] || 0,
          volumeNumeric: parseFloat(ticker.volume)
        }));

        const sortedData = mappedData.sort((a, b) => b.volumeNumeric - a.volumeNumeric);
        setTickers(sortedData);
      } catch (error) {
        console.error("Error fetching tickers:", error);
      }
    };

    fetchTickers();
    const interval = setInterval(fetchTickers, 5000);
    return () => clearInterval(interval);
  }, [holdings]);

  useEffect(() => {
    fetchUserData();
    fetchTransactions();
  }, [fetchUserData, fetchTransactions]);

  const formatNumber = (number) => {
    if (!number || isNaN(number)) return "0";
    
    const num = typeof number === 'string' ? parseFloat(number) : number;
    
    if (num === 0) return "0";
    if (Math.abs(num) < 0.0001) return num.toFixed(8);
    if (Math.abs(num) < 1) return num.toFixed(4);
    
    return new Intl.NumberFormat("en-US", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(num);
  };

  const handleConfirm = async (action) => {
    if (!quantity || isNaN(quantity) || quantity <= 0) {
      setError("Please enter a valid quantity.");
      return;
    }
  
    const endpoint = action === "buy" ? "/buy" : "/sell";
    const payload = {
      symbol: selectedTicker.symbol,
      quantity: parseFloat(quantity),
      price: selectedTicker.price
    };
  
    try {
      const response = await fetch(`http://localhost:8080/api${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
  
      const result = await response.json();
  
      if (!result.success) {
        throw new Error(result.message || "Transaction failed.");
      }
  
      fetchUserData();
      fetchTransactions();

      alert(`${action === "buy" ? "Purchase" : "Sale"} successful!`);
      setSelectedTicker(null);
      setQuantity("");
      setError("");
      
    } catch (error) {
      setError(error.message);
    }
  };

  const handleCancel = () => {
    setSelectedTicker(null);
    setQuantity("");
    setError("");
  };

  const resetBalance = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/reset", {
        method: "POST",
        headers: { "Content-Type": "application/json" }
      });
      
      const result = await response.json();
      
      if (result.success) {
        setBalance(10000);
        setHoldings({});
        alert("Your account has been reset to $10,000");
        
        fetchTransactions();
      } else {
        alert("Failed to reset account");
      }
    } catch (error) {
      console.error("Error resetting account:", error);
      alert("Failed to reset account");
    }
  };

  return (
    <div className="bg-gray-900 text-white min-h-screen">
      <Header />
      <div className="p-6">
        <div className="overflow-x-auto shadow-lg rounded-lg max-w-5xl mx-auto">
          <div className="flex justify-between items-center mb-4">
            <div className="flex items-center">
              <h2 className="text-lg font-semibold text-gray-300">
                Balance: ${formatNumber(balance ? balance.toFixed(2) : "0.00")}
              </h2>
              <button
                onClick={resetBalance}
                className="ml-4 bg-yellow-600 hover:bg-yellow-700 text-white text-xs px-3 py-1 rounded flex items-center"
                title="Reset account to $10,000"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth={1.5}
                  stroke="currentColor"
                  className="w-4 h-4 mr-1"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99"
                  />
                </svg>
                Reset
              </button>
            </div>
            <div className="flex space-x-2">
              <button
                onClick={() => setShowHistory(true)}
                className="bg-gray-500 hover:bg-blue-600 text-white px-3 py-2 rounded flex items-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth={1.5}
                  stroke="currentColor"
                  className="w-6 h-6"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="m20.25 7.5-.625 10.632a2.25 2.25 0 0 1-2.247 2.118H6.622a2.25 2.25 0 0 1-2.247-2.118L3.75 7.5M10 11.25h4M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125Z"
                  />
                </svg>
              </button>
              <button
                onClick={() => setShowHoldings(!showHoldings)}
                className="bg-gray-500 hover:bg-blue-600 text-white px-3 py-2 rounded flex items-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth={1.5}
                  stroke="currentColor"
                  className="w-6 h-6"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M2.25 18.75a60.07 60.07 0 0 1 15.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75A.75.75 0 0 1 3 6h-.75m0 0v-.375c0-.621.504-1.125 1.125-1.125H20.25M2.25 6v9m18-10.5v.75c0 .414.336.75.75.75h.75m-1.5-1.5h.375c.621 0 1.125.504 1.125 1.125v9.75c0 .621-.504 1.125-1.125 1.125h-.375m1.5-1.5H21a.75.75 0 0 0-.75.75v.75m0 0H3.75m0 0h-.375a1.125 1.125 0 0 1-1.125-1.125V15m1.5 1.5v-.75A.75.75 0 0 0 3 15h-.75M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Zm3 0h.008v.008H18V10.5Zm-12 0h.008v.008H6V10.5Z"
                  />
                </svg>
              </button>
            </div>
          </div>
          
          {/* Ticker table */}
          <table className="min-w-full bg-gray-800 text-gray-200">
            <thead>
              <tr className="bg-gray-700 text-gray-300">
                <th className="px-6 py-4 text-sm font-semibold uppercase">#</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">Symbol</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">Price</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">24h High</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">24h Low</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">24h Change</th>
                {showHoldings && (
                  <>
                    <th className="px-6 py-4 text-sm font-semibold uppercase">Owned</th>
                    <th className="px-6 py-4 text-sm font-semibold uppercase">Value (USD)</th>
                  </>
                )}
              </tr>
            </thead>
            <tbody>
              {tickers.map((ticker, index) => (
                <tr
                  key={index}
                  className="border-t border-gray-700 hover:bg-gray-700 transition duration-200"
                  onClick={() => setSelectedTicker(ticker)}
                >
                  <td className="px-6 py-4 text-sm font-medium">{index + 1}</td>
                  <td className="px-6 py-4 text-sm font-medium">{ticker.symbol}</td>
                  <td className="px-6 py-4 text-sm">${formatNumber(ticker.price)}</td>
                  <td className="px-6 py-4 text-sm">${formatNumber(ticker.high.toFixed(2))}</td>
                  <td className="px-6 py-4 text-sm">${formatNumber(ticker.low.toFixed(2))}</td>
                  <td
                    className={`px-6 py-4 text-sm ${
                      ticker.change > 0 ? "text-green-500" : "text-red-500"
                    }`}
                  >
                    {Math.abs(ticker.change).toFixed(2)}%
                  </td>
                  {showHoldings && (
                    <>
                      <td className="px-6 py-4 text-sm font-medium">
                        {ticker.quantityOwned > 0 ? 
                          ticker.quantityOwned < 0.01 ? 
                            ticker.quantityOwned.toFixed(8) : 
                            ticker.quantityOwned.toFixed(4)
                          : "0"}
                      </td>
                      <td className="px-6 py-4 text-sm font-medium">
                        {formatNumber((ticker.quantityOwned * ticker.price))}
                      </td>
                    </>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Transaction history modal */}
      {showHistory && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-gray-800 p-6 rounded-lg shadow-lg w-3/4 max-h-[80vh] overflow-y-auto">
            <h2 className="text-lg font-semibold text-white mb-4">Transaction History</h2>
            <table className="table-auto w-full text-left border-collapse bg-gray-700 rounded-lg">
              <thead>
                <tr className="bg-gray-600 text-gray-300">
                  <th className="px-6 py-4 text-sm font-semibold uppercase">Timestamp</th>
                  <th className="px-6 py-4 text-sm font-semibold uppercase">Action</th>
                  <th className="px-6 py-4 text-sm font-semibold uppercase">Symbol</th>
                  <th className="px-6 py-4 text-sm font-semibold uppercase">Quantity</th>
                  <th className="px-6 py-4 text-sm font-semibold uppercase">Price</th>
                  <th className="px-6 py-4 text-sm font-semibold uppercase">Profit/Loss</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((transaction, index) => (
                  <tr key={index} className="border-t border-gray-600 hover:bg-gray-600">
                    <td className="px-6 py-4 text-sm">{transaction.timestamp}</td>
                    <td className="px-6 py-4 text-sm">{transaction.action}</td>
                    <td className="px-6 py-4 text-sm">{transaction.symbol}</td>
                    <td className="px-6 py-4 text-sm">{transaction.quantity}</td>
                    <td className="px-6 py-4 text-sm">${formatNumber(transaction.price)}</td>
                    <td className="px-6 py-4 text-sm">
                      {transaction.profitLoss !== null
                        ? `$${formatNumber(transaction.profitLoss)}`
                        : "N/A"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="flex justify-end mt-4">
              <button
                onClick={() => setShowHistory(false)}
                className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Trade modal */}
      <TradeModal
        selectedTicker={selectedTicker}
        quantity={quantity}
        setQuantity={setQuantity}
        handleConfirm={handleConfirm}
        handleCancel={handleCancel}
        error={error}
        formatNumber={formatNumber}
        balance={balance}
      />
    </div>
  );
};

export default Home;