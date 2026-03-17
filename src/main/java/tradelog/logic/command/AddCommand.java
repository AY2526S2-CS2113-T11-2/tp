package tradelog.logic.command;

import java.util.HashMap;
import tradelog.exception.TradeLogException;
import tradelog.logic.parser.ArgumentTokeniser;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Represents a command to add a new trade to the TradeLog.
 * Handles parsing, strict validation of user arguments, and executing the addition.
 */
public class AddCommand extends Command{
    private final HashMap<String, String> parsedArgs;
    private static final String[] REQUIRED_PREFIXES = {
            "t/", "d/", "dir/", "e/", "x/", "s/", "o/", "strat/"
    };

    /**
     * Constructs an AddCommand by parsing and validating the raw arguments string.
     * Strictly checks that all required prefixes are present and not empty.
     *
     * @param arguments The raw string after the "add" command word.
     * @throws TradeLogException If any required prefix is missing or blank.
     */
    public AddCommand(String arguments) throws TradeLogException {
        parsedArgs = ArgumentTokeniser.tokenise(arguments, REQUIRED_PREFIXES);

        for (String prefix : REQUIRED_PREFIXES){
            if (!parsedArgs.containsKey(prefix)){
                throw new TradeLogException("Missing required prefix: " + prefix);
            }
            if (parsedArgs.get(prefix).trim().isEmpty()){
                throw new TradeLogException("The value for " + prefix + " cannot be empty.");
            }
        }
    }

    /**
     * Executes the add command by creating a new Trade object, adding it to the TradeList,
     * and displaying the trade summary to the user.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        try {
            // Convert price strings to doubles
            double entry = Double.parseDouble(parsedArgs.get("e/"));
            double exit = Double.parseDouble(parsedArgs.get("x/"));
            double stop = Double.parseDouble(parsedArgs.get("s/"));

            String ticker = parsedArgs.get("t/").toUpperCase();
            String rawDir = parsedArgs.get("dir/");
            String direction = rawDir.substring(0, 1).toUpperCase() + rawDir.substring(1).toLowerCase();

            Trade newTrade = new Trade(
                    ticker,
                    parsedArgs.get("d/"),
                    direction,
                    entry,
                    exit,
                    stop,
                    parsedArgs.get("o/"),
                    parsedArgs.get("strat/")
            );

            tradeList.addTrade(newTrade);

            System.out.println(newTrade.toSummaryString());
            System.out.println("Trade successfully added.");

        } catch (NumberFormatException e) {
            System.out.println("Error: Entry, Exit, and Stop Loss must be valid numbers!");
        }
    }
}
