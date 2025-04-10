import React, { useState, useEffect } from "react";

const TradeModal = ({
  selectedTicker,
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
  const [isUpdating, setIsUpdating] = useState(false);

  useEffect(() => {
    if (selectedTicker && !isUpdating) {
      setIsUpdating(true);
      setUsdAmount((parseFloat(quantity || 0) * (selectedTicker.price || 0)).toFixed(2));
      setTimeout(() => setIsUpdating(false), 0);
    }
  }, [selectedTicker, quantity]);

  useEffect(() => {
    if (!selectedTicker || !selectedTicker.price || isUpdating) return;

    setIsUpdating(true);
    if (lastUpdatedField === "usd" && usdAmount) {
      setQuantity((parseFloat(usdAmount) / selectedTicker.price).toFixed(8));
    } else if (lastUpdatedField === "crypto" && quantity) {
      setUsdAmount((parseFloat(quantity) * selectedTicker.price).toFixed(2));
    }
    
    setTimeout(() => setIsUpdating(false), 0);
  }, [usdAmount, quantity, selectedTicker, setQuantity, lastUpdatedField]);

  if (!selectedTicker) return null;

  const price = selectedTicker.price || 0;
  const symbolName = selectedTicker.symbol.split('/')[0];
  const totalCost = quantity ? parseFloat(quantity) * price : 0;
  
  const handleMaxQuantity = () => {
    const owned = selectedTicker.quantityOwned || 0;
    if (owned > 0) {
      setQuantity(owned.toString());
      setLastUpdatedField("crypto");
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-10 flex items-center justify-center backdrop-blur-sm">
      <div className="bg-gray-800 p-6 rounded-lg shadow-lg w-96">
        <h2 className="text-lg font-semibold mb-4">
          Trade {selectedTicker.symbol}
        </h2>
        <div className="text-lg font-semibold mb-2">
          ${price < 0.0001 ? price.toFixed(8) : formatNumber(price)} per {symbolName}
        </div>
        
        {/* Input fields */}
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
          <label className="block text-sm font-medium mb-1">Quantity ({symbolName})</label>
          <div className="relative">
            <input
              type="number"
              value={quantity || ""}
              onChange={(e) => {
                setQuantity(e.target.value);
                setLastUpdatedField("crypto");
              }}
              className="w-full px-4 py-2 rounded bg-gray-700 text-white appearance-none pr-16"
            />
            <button
              onClick={handleMaxQuantity}
              className="absolute right-2 top-1/2 transform -translate-y-1/2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold px-2 py-1 rounded"
            >
              MAX
            </button>
          </div>
          {selectedTicker.quantityOwned > 0 && (
            <p className="text-xs text-gray-400 mt-1">
              Available: {selectedTicker.quantityOwned < 0.01 
                ? selectedTicker.quantityOwned.toFixed(8)
                : selectedTicker.quantityOwned.toFixed(4)}
            </p>
          )}
        </div>
        
        {/* Cost and balance display */}
        <div className="mb-4 flex justify-between">
          <div>
            <label className="block text-md font-medium mb-1">Total Cost</label>
            <p className="text-gray-300">
              ${totalCost < 0.0001 ? totalCost.toFixed(8) : formatNumber(totalCost)}
            </p>
          </div>
          <div>
            <label className="block text-md font-medium mb-1">Balance</label>
            <p className="text-gray-300">
              ${formatNumber((balance || 0).toFixed(2))}
            </p>
          </div>
        </div>
        
        {/* Error message */}
        {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
        
        {/* Action buttons */}
        <div className="flex justify-between">
          <div className="space-x-2">
            <button
              onClick={() => handleConfirm("buy")}
              className="bg-green-500 hover:bg-green-600 text-white px-5 py-2 rounded"
            >
              Buy
            </button>
            <button
              onClick={() => handleConfirm("sell")}
              className="bg-red-500 hover:bg-red-600 text-white px-5 py-2 rounded"
              disabled={!selectedTicker.quantityOwned || selectedTicker.quantityOwned <= 0}
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