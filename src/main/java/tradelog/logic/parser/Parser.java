package tradelog.logic.parser;

import tradelog.exception.TradeLogException;
import tradelog.logic.command.AddCommand;
import tradelog.logic.command.CompareCommand;
import tradelog.logic.command.Command;
import tradelog.logic.command.DeleteCommand;
import tradelog.logic.command.EditCommand;
import tradelog.logic.command.ExitCommand;
import tradelog.logic.command.ListCommand;
import tradelog.logic.command.SummaryCommand;
import tradelog.logic.command.FilterCommand;
import tradelog.logic.command.UndoCommand;

/**
 * Parses raw user input and translates it into executable Command objects.
 */
public class Parser {

    /**
     * Parses user input into a command for execution.
     *
     * @param userInput The full user input string.
     * @return The command based on the user input.
     * @throws TradeLogException If the command is unknown or arguments are invalid.
     */
    public static Command parseCommand(String userInput) throws TradeLogException {
        String[] splitInput = userInput.trim().split(" ", 2);
        String commandWord = splitInput[0].toLowerCase();
        String arguments = splitInput.length > 1 ? splitInput[1] : "";

        return switch (commandWord) {
        case "list" -> new ListCommand();
        case "add" -> new AddCommand(arguments);
        case "delete" -> new DeleteCommand(arguments);
        case "edit" -> new EditCommand(arguments);
        case "filter" -> new FilterCommand(arguments);
        case "compare" -> new CompareCommand();
        case "summary" -> new SummaryCommand();
        case "undo" -> new UndoCommand();
        case "exit" -> new ExitCommand();
        default -> throw new TradeLogException(
                "Unknown command: '" + commandWord + "'. Please try again.");
        };
    }
}
