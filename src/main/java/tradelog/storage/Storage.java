package tradelog.storage;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

/**
 * Handles reading and writing of trade data to and from a file.
 */
public class Storage {
    private String filePath;

    /**
     * Constructs a Storage instance with the specified file path.
     * 
     * @param filePath
    */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the given TradeList to the file specified by filePath.
     * 
     * @param tradeList
     */
    public void saveTrades(TradeList tradeList) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int i = 0; i < tradeList.size(); i++) {
                Trade trade = tradeList.getTrade(i);
                writer.write(trade.toStorageString());
                writer.write("\n");
            }

            System.out.println("Trades saved successfully!");
        } catch (IOException e) {
            System.err.println("Failed to save trades: " + e.getMessage());
        }
    }

    /**
     *  Loads trades from the file specified by filePath and returns them as a TradeList.
     * 
     * @return A TradeList containing all trades loaded from the file.
     */
    public TradeList loadTrades() throws TradeLogException {
        TradeList tradeList = new TradeList();
        File file = new File(filePath);
        
        if (!file.exists()) {
            return tradeList; // Return empty list if file doesn't exist
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split(" \\| ");
                if (parts.length == 8) {
                    String ticker = parts[0];
                    String date = parts[1];
                    String direction = parts[2];
                    double entryPrice = Double.parseDouble(parts[3]);
                    double exitPrice = Double.parseDouble(parts[4]);
                    double stopLossPrice = Double.parseDouble(parts[5]);
                    String outcome = parts[6];
                    String strategy = parts[7];
                    
                    Trade trade = new Trade(ticker, date, direction, entryPrice, exitPrice, 
                                            stopLossPrice, outcome, strategy);
                    tradeList.addTrade(trade);
                }
            }
            
            System.out.println("Trades loaded successfully!");
        } catch (IOException e) {
            throw new TradeLogException("Failed to load trades");
        }
        
        return tradeList;
    }
}
