package tradelog.logic.parser;

import tradelog.exception.TradeLogException;
import tradelog.logic.command.AddCommand;
import tradelog.logic.command.Command;
import tradelog.logic.command.DeleteCommand;
import tradelog.logic.command.EditCommand;
import tradelog.logic.command.ExitCommand;
import tradelog.logic.command.ListCommand;
import tradelog.logic.command.SummaryCommand;

/**
 * Parses raw user input and translates it into executable Command objects.
 */
public class Parser {
    /**
     * Parses user input into a command for execution.
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws TradeLogException If the command is unknown or arguments are invalid.
     */
    public static Command parseCommand(String userInput) throws TradeLogException {
        String[] splitInput = userInput.trim().split(" ", 2);
        String commandWord = splitInput[0].toLowerCase();
        String arguments = splitInput.length > 1 ? splitInput[1] : "";

        switch (commandWord) {
        case "list":
            return new ListCommand();

        case "add":
            return new AddCommand(arguments);

        case "delete":
            return new DeleteCommand(arguments);

        case "edit":
            return new EditCommand(arguments);

        case "summary":
            return new SummaryCommand();

        case "exit":
            return new ExitCommand();

        default:
            throw new TradeLogException("Unknown command: '" + commandWord + "'. Please try again.");
        }
    }
}
