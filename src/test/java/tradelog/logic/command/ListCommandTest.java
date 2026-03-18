package tradelog.logic.command;

import org.junit.jupiter.api.Test;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListCommandTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(buffer));
        action.run();
        System.setOut(original);
        return buffer.toString();
    }

    @Test
    public void execute_emptyTradeList_showsEmptyMessage() {
        TradeList tradeList = new TradeList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");
        ListCommand command = new ListCommand();
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));
        assertTrue(output.contains("No trades logged yet."));
    }

    @Test
    public void isExit_listCommand_returnsFalse() {
        assertFalse(new ListCommand().isExit());
    }
}
