package tradelog.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeListTest {

    @Test
    public void isEmpty_newTradeList_returnsTrue() {
        assertTrue(new TradeList().isEmpty());
    }

    @Test
    public void size_newTradeList_returnsZero() {
        assertEquals(0, new TradeList().size());
    }
}
