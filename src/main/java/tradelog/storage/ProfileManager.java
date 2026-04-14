package tradelog.storage;

import java.io.File;
import tradelog.exception.TradeLogException;
import tradelog.model.TradeList;
import tradelog.ui.Ui;

/**
 * Manages the initialisation of storage profiles based on user passwords.
 * When a user enters a password, ProfileManager checks for existing storage files
 * matching that password.
 * If found, it loads the corresponding trades. If not, it asks for confirmation
 * before creating a new profile, and re-prompts for the password if the user declines.
 */
public class ProfileManager {
    private Storage activeStorage;
    private TradeList loadedTrades;

    public ProfileManager(String baseDir, String baseName, Ui ui) {
        while (true) {
            boolean anyProfileExists = new File(baseDir + "/" + baseName + ".txt").exists();
            String prompt = anyProfileExists
                    ? "\n Enter password to load your profile (or create a new one): "
                    : "\n No profiles found. Create a new password: ";
            String password = ui.readPassword(prompt);
            if (password == null) {
                throw new IllegalStateException("Input closed during profile selection.");
            }
            if (password.trim().isEmpty()) {
                ui.showError("Password cannot be empty.");
                continue;
            }

            if (!anyProfileExists) {
                createNewProfile(baseDir, baseName, password, ui,
                        "\n No existing profile found. Creating new profile...");
                break;
            }

            if (tryLoadExistingProfile(baseDir, baseName, password, ui)) {
                break;
            }

            String confirm = ui.readLine(
                    "\n No profile found for the entered password."
                    + " Create a new profile? (yes/no): ");
            if (confirm == null) {
                throw new IllegalStateException("Input closed during profile selection.");
            }
            if (confirm.equalsIgnoreCase("yes")) {
                createNewProfile(baseDir, baseName, password, ui, "\n Creating new profile...");
                break;
            }
        }
    }

    private boolean tryLoadExistingProfile(String baseDir, String baseName, String password, Ui ui) {
        int fileIndex = 0;
        while (true) {
            String suffix = (fileIndex == 0) ? "" : "_" + fileIndex;
            String currentPath = baseDir + "/" + baseName + suffix + ".txt";
            File currentFile = new File(currentPath);

            if (!currentFile.exists()) {
                return false;
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
                return true;
            } catch (TradeLogException e) {
                fileIndex++;
            }
        }
    }

    private void createNewProfile(String baseDir, String baseName,
            String password, Ui ui, String message) {
        String newPath = findNextAvailablePath(baseDir, baseName);
        ui.showMessage(message);
        activeStorage = new Storage(newPath);
        try {
            activeStorage.setPassword(password);
        } catch (TradeLogException e) {
            ui.showError("Security initialisation failed: " + e.getMessage());
            System.exit(1);
        }
        loadedTrades = new TradeList();
    }

    private static String findNextAvailablePath(String baseDir, String baseName) {
        int i = 0;
        while (true) {
            String suffix = (i == 0) ? "" : "_" + i;
            String path = baseDir + "/" + baseName + suffix + ".txt";
            if (!new File(path).exists()) {
                return path;
            }
            i++;
        }
    }

    public Storage getActiveStorage() {
        return activeStorage;
    }

    public TradeList getLoadedTrades() {
        return loadedTrades;
    }
}
