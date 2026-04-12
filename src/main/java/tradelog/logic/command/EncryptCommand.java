package tradelog.logic.command;

import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Toggles encryption on or off for the stored trade data.
 */
public class EncryptCommand extends Command {

    private final String argument;

    public EncryptCommand(String argument) {
        this.argument = argument.trim().toLowerCase();
    }

    /**
     * Allows user to enable or disable encryption for stored trade data.
     * When enabled, trades will be encrypted before being saved to the file.
     * When disabled, trades will be saved in plaintext.
     * The user can also check the current encryption status.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        switch (argument) {
        case "on":
            storage.setEncryptionEnabled(true);
            ui.showMessage("Encryption enabled. Your trades will be encrypted when saved.");
            break;
        case "off":
            storage.setEncryptionEnabled(false);
            ui.showMessage("Encryption disabled. Your trades will be saved in plaintext.");
            break;
        case "status":
            String status = storage.isEncryptionEnabled() ? "enabled" : "disabled";
            ui.showMessage("Encryption is currently " + status + ".");
            break;
        default:
            ui.showError("Invalid argument. Usage: encrypt [on|off|status]");
            break;
        }
    }
}
