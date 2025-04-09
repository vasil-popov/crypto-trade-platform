import React, { useState, useEffect } from "react";

const TradeModal = ({
  selectedTicker,
  modalType,
  quantity,
  setQuantity,
  handleConfirm,
  handleCancel,
  error,
  formatNumber,
  balance,
}) => {
  const [usdAmount, setUsdAmount] = useState("");
  const [lastUpdatedField, setLastUpdatedField] = useState(null);

  useEffect(() => {
    if (lastUpdatedField === "usd" && usdAmount && selectedTicker) {
      setQuantity((usdAmount / selectedTicker.last).toFixed(8));
    }
  }, [usdAmount, selectedTicker, setQuantity, lastUpdatedField]);

  useEffect(() => {
    if (lastUpdatedField === "crypto" && quantity && selectedTicker) {
      setUsdAmount((quantity * selectedTicker.last).toFixed(2));
    }
  }, [quantity, selectedTicker, lastUpdatedField]);

  if (!selectedTicker) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-10 flex items-center justify-center backdrop-blur-sm">
      <div className="bg-gray-800 p-6 rounded-lg shadow-lg w-96">
        <h2 className="text-lg font-semibold mb-4">
          {modalType === "buy" ? "Buy" : "Sell"} {selectedTicker.symbol}
        </h2>
        <div className="mb-4">
          <label className="block text-sm font-medium mb-1">Amount in USD</label>
          <input
            type="number"
            value={usdAmount}
            onChange={(e) => {
              setUsdAmount(e.target.value);
              setLastUpdatedField("usd");
            }}
            className="w-full px-4 py-2 rounded bg-gray-700 text-white appearance-none"
          />
        </div>
        <div className="mb-4">
          <label className="block text-sm font-medium mb-1">Quantity ({selectedTicker.symbol})</label>
          <input
            type="number"
            value={quantity || 0} // Fallback to 0 if undefined
            onChange={(e) => {
              setQuantity(e.target.value);
              setLastUpdatedField("crypto");
            }}
            className="w-full px-4 py-2 rounded bg-gray-700 text-white appearance-none"
          />
        </div>
        <div className="mb-4 flex justify-start">
          <div>
            <label className="block text-md font-medium mb-1">Total Cost</label>
            <p className="text-gray-300">
              ${formatNumber((quantity * selectedTicker?.last || 0).toFixed(2))}
            </p>
          </div>
          <div>
            <label className="block text-md font-medium mb-1 mx-10">Balance</label>
            <p className="text-gray-300 mx-10">
              ${formatNumber((balance || 0).toFixed(2))}
            </p>
          </div>
        </div>
        {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
        <div className="flex justify-between">
          <div>
            <button
              onClick={() => handleConfirm("buy")}
              className="bg-green-500 hover:bg-green-600 text-white px-5 py-2 rounded mr-2"
            >
              Buy
            </button>
            <button
              onClick={() => handleConfirm("sell")}
              className="bg-red-500 hover:bg-red-600 text-white px-5 py-2 rounded"
            >
              Sell
            </button>
          </div>
          <button
            onClick={handleCancel}
            className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default TradeModal;