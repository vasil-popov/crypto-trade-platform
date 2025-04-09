import React, { useEffect, useState } from "react";
import Header from "../components/Header";

const Home = () => {
  const [tickers, setTickers] = useState([]);

  useEffect(() => {
    const fetchTickers = async () => {
      try {
        const response = await fetch("http://localhost:8080/tickers");
        const data = await response.json();

        const sortedData = data
          .map((ticker) => ({
            ...ticker,
            marketCap: ticker.last * ticker.volume,
          }))
          .sort((a, b) => b.marketCap - a.marketCap);

        setTickers(sortedData);
      } catch (error) {
        console.error("Error fetching tickers:", error);
      }
    };

    fetchTickers();
    const interval = setInterval(fetchTickers, 5000);

    return () => clearInterval(interval);
  }, []);

  const formatNumber = (number) => {
    return new Intl.NumberFormat("en-US").format(number);
  };

  return (
    <div className="bg-gray-900 text-white min-h-screen">
      <Header />
      <div className="p-6">
        <div className="overflow-x-auto shadow-lg rounded-lg max-w-5xl mx-auto">
          <table className="table-auto w-full text-left border-collapse bg-gray-800 rounded-lg">
            <thead>
              <tr className="bg-gray-700 text-gray-300">
                <th className="px-6 py-4 text-sm font-semibold uppercase">#</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">Symbol</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">Price</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">24h High</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">24h Low</th>
                <th className="px-6 py-4 text-sm font-semibold uppercase">24h Change</th>
              </tr>
            </thead>
            <tbody>
              {tickers.map((ticker, index) => (
                <tr
                  key={index}
                  className="border-t border-gray-700 hover:bg-gray-700 transition duration-200"
                >
                  <td className="px-6 py-4 text-sm font-medium">{index + 1}</td>
                  <td className="px-6 py-4 text-sm font-medium">{ticker.symbol}</td>
                  <td className="px-6 py-4 text-sm">${formatNumber(ticker.last.toFixed(8))}</td>
                  <td className="px-6 py-4 text-sm">${formatNumber(ticker.high.toFixed(2))}</td>
                  <td className="px-6 py-4 text-sm">${formatNumber(ticker.low.toFixed(2))}</td>
                  <td
                    className={`px-6 py-4 text-sm ${
                      ticker.change_pct > 0 ? "text-green-500" : "text-red-500"
                    }`}
                  >
                    {Math.abs(ticker.change_pct.toFixed(2))}%
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Home;