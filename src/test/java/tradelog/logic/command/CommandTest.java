package tradelog.logic.command;

import org.junit.jupiter.api.Test;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CommandTest {

    /**
     * Concrete subclass of Command used purely for testing the base class behaviour.
     */
    private static class ConcreteCommand extends Command {
        @Override
        public void execute(TradeList tradeList, Ui ui, Storage storage) {
            // no-op for testing
        }
    }

    @Test
    public void isExit_defaultCommand_returnsFalse() {
        assertFalse(new ConcreteCommand().isExit());
    }
}
