package tradelog.storage;

import java.io.File;
import tradelog.exception.TradeLogException;
import tradelog.model.TradeList;
import tradelog.ui.Ui;

/**
 * Manages the initialisation of storage profiles based on user passwords.
 * When a user enters a password, ProfileManager checks for existing storage files
 * matching that password.
 * If found, it loads the corresponding trades. If not, it creates a new profile.
 */
public class ProfileManager {
    private Storage activeStorage;
    private TradeList loadedTrades;

    public ProfileManager(String baseDir, String baseName, String password, Ui ui) {
        int fileIndex = 0;
        while (true) {
            String suffix = (fileIndex == 0) ? "" : "_" + fileIndex;
            String currentPath = baseDir + "/" + baseName + suffix + ".txt";
            File currentFile = new File(currentPath);
            
            // TODO: Prompt user to retry password entry if file exists but password is wrong,
            // instead of creating new profile immediately
            if (!currentFile.exists()) {
                String message = (fileIndex == 0) ? ("No existing profile found. Creating new profile...") :
                    ("No profile found for the entered password. Creating new profile...");
                ui.showMessage(message);
                activeStorage = new Storage(currentPath);
                try {
                    activeStorage.setPassword(password);
                } catch (TradeLogException e) {
                    ui.showError("Security initialisation failed: " + e.getMessage());
                    System.exit(1);
                }
                loadedTrades = new TradeList();
                break;
            }

            Storage tempStorage = new Storage(currentPath);
            try {
                tempStorage.setPassword(password);
                loadedTrades = tempStorage.loadTrades();
                
                activeStorage = tempStorage;
                ui.showMessage("Successfully retrieved profile!");
                if (!loadedTrades.isEmpty()) {
                    ui.showMessage("Loaded " + loadedTrades.size() + " trade(s) from storage.");
                }
                break; 
                
            } catch (TradeLogException e) {
                fileIndex++; 
            }
        }
    }

    public Storage getActiveStorage() {
        return activeStorage;
    }

    public TradeList getLoadedTrades() {
        return loadedTrades;
    }
}
