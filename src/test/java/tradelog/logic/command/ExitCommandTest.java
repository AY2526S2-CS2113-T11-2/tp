package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ExitCommandTest {

    @Test
    public void isExit_exitCommand_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }
}
