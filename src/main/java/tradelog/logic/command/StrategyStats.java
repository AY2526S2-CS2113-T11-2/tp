package tradelog.logic.command;

/**
 * Stores aggregate performance metrics for a single strategy.
 */
public class StrategyStats {

    private int tradeCount;
    private int winCount;
    private int lossCount;
    private double totalWinR;
    private double totalLossR;

    /**
     * Records a trade result for this strategy.
     *
     * @param riskRewardRatio The trade's R-multiple.
     */
    public void addTrade(double riskRewardRatio) {
        assert !Double.isNaN(riskRewardRatio) : "Risk-reward ratio should not be NaN";
        assert !Double.isInfinite(riskRewardRatio) : "Risk-reward ratio should be finite";

        tradeCount++;

        if (riskRewardRatio > 0) {
            winCount++;
            totalWinR += riskRewardRatio;
            return;
        }

        if (riskRewardRatio < 0) {
            lossCount++;
            totalLossR += Math.abs(riskRewardRatio);
        }
    }

    /**
     * Returns the number of trades recorded for this strategy.
     *
     * @return The number of trades.
     */
    public int getTradeCount() {
        assert tradeCount >= 0 : "Trade count should be non-negative";
        return tradeCount;
    }

    /**
     * Returns the win rate percentage for this strategy.
     *
     * @return The win rate as a percentage.
     */
    public double getWinRate() {
        if (tradeCount == 0) {
            return 0;
        }
        double winRate = ((double) winCount / tradeCount) * 100;
        assert winRate >= 0 && winRate <= 100 : "Win rate should be between 0 and 100";
        return winRate;
    }

    /**
     * Returns the average positive R-multiple for this strategy.
     *
     * @return The average win.
     */
    public double getAverageWin() {
        if (winCount == 0) {
            return 0;
        }
        double averageWin = totalWinR / winCount;
        assert averageWin >= 0 : "Average win should be non-negative";
        return averageWin;
    }

    /**
     * Returns the average losing R-multiple as a positive value.
     *
     * @return The average loss.
     */
    public double getAverageLoss() {
        if (lossCount == 0) {
            return 0;
        }
        double averageLoss = totalLossR / lossCount;
        assert averageLoss >= 0 : "Average loss should be non-negative";
        return averageLoss;
    }

    /**
     * Returns the expected value for this strategy.
     *
     * @return The expected value in R.
     */
    public double getExpectedValue() {
        if (tradeCount == 0) {
            return 0;
        }
        double expectedValue = (totalWinR - totalLossR) / tradeCount;
        assert !Double.isNaN(expectedValue) : "Expected value should not be NaN";
        assert !Double.isInfinite(expectedValue) : "Expected value should be finite";
        return expectedValue;
    }
}
