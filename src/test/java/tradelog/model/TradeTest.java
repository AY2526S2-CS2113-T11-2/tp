package tradelog.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the mathematical calculations and string formatting of the Trade class.
 */
public class TradeTest {

    /**
     * Tests if the Risk:Reward ratio is calculated correctly for a Long trade.
     * Risk = 10 (180 - 170), Reward = 10 (190 - 180). Expected RR = 1.0.
     */
    @Test
    public void getRiskRewardRatio_longTrade_calculatesCorrectly() {
        Trade trade = new Trade("AAPL", "2026-02-18", "Long", 180.0, 190.0, 170.0, "Breakout");
        assertEquals(1.0, trade.getRiskRewardRatio(), 0.001);
    }

    /**
     * Tests if the Risk:Reward ratio is calculated correctly for a Short trade.
     * Risk = 10 (190 - 180), Reward = 10 (180 - 170). Expected RR = 1.0.
     */
    @Test
    public void getRiskRewardRatio_shortTrade_calculatesCorrectly() {
        Trade trade = new Trade("TSLA", "2026-02-17", "Short", 180.0, 170.0, 190.0, "Pullback");
        assertEquals(1.0, trade.getRiskRewardRatio(), 0.001);
    }

    /**
     * Tests if the system safely returns 0 when the risk is 0 to prevent a division by zero crash.
     */
    @Test
    public void getRiskRewardRatio_zeroRisk_returnsZero() {
        Trade trade = new Trade("EURUSD", "2026-02-16", "Long", 1.33, 1.34, 1.33, "Range");
        assertEquals(0.0, trade.getRiskRewardRatio(), 0.001);
    }

    /**
     * Tests if the trade summary string perfectly matches the expected output formatting.
     */
    @Test
    public void toSummaryString_validTrade_formatsCorrectly() {
        Trade trade = new Trade("AAPL", "2026-02-18", "Long", 180.0, 190.0, 170.0, "Breakout");

        String expectedOutput = "Trade Summary:\n" +
                "Ticker: AAPL\n" +
                "Date: 2026-02-18\n" +
                "Direction: Long\n" +
                "Entry: 180\n" +
                "Exit: 190\n" +
                "Stop: 170\n" +
                "Strategy: Breakout\n\n" +
                "Risk:Reward: +1.00R";

        assertEquals(expectedOutput, trade.toSummaryString());
    }
}
