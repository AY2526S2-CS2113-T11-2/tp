package tradelog.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;

/**
 * Test suite for the {@link Storage} class.
 * Uses JUnit's @TempDir to safely test file reading and writing without
 * permanently altering the local file system or leaving behind test artifacts.
 */
public class StorageTest {

    /**
     * JUnit automatically creates this temporary directory before tests
     * and securely deletes it after the tests finish.
     */
    @TempDir
    Path tempDir;

    /** The storage instance used for testing. */
    private Storage storage;

    /** The path to the temporary test file. */
    private String testFilePath;

    /**
     * Sets up the file path inside the temporary directory before each test.
     */
    @BeforeEach
    public void setUp() throws TradeLogException {
        // Resolve a file name inside the temporary directory
        testFilePath = tempDir.resolve("test_tradelog.txt").toString();
        storage = new Storage(testFilePath);
        storage.setPassword("testpassword");
    }

    /**
     * Tests that saving a populated TradeList and subsequently loading it
     * retrieves the exact same data, ensuring the write/read formats match.
     *
     * @throws TradeLogException If file I/O operations fail.
     */
    @Test
    public void saveAndLoadTrades_validData_successfulIntegration() throws TradeLogException {
        TradeList originalList = new TradeList();
        Trade trade = new Trade("AAPL", "2023-10-10", "long",
                150.0, 160.0, 140.0, "WIN", "Trend");
        originalList.addTrade(trade);
        storage.saveTrades(originalList);
        TradeList loadedList = storage.loadTrades();

        assertEquals(1, loadedList.size(), "Loaded list should contain exactly 1 trade.");

        Trade loadedTrade = loadedList.getTrade(0);
        assertEquals("AAPL", loadedTrade.getTicker(), "Ticker should match the saved trade.");
        assertEquals(150.0, loadedTrade.getEntryPrice(), "Entry price should match.");
        assertEquals("WIN", loadedTrade.getOutcome(), "Outcome should match.");
    }

    /**
     * Tests the boundary condition where the storage file does not exist yet.
     * Verifies that the storage class safely returns an empty TradeList
     * instead of throwing a NullPointerException or crashing.
     *
     * @throws TradeLogException If an unexpected read error occurs.
     */
    @Test
    public void loadTrades_nonExistentFile_returnsEmptyList() throws TradeLogException {
        File file = new File(testFilePath);
        assertTrue(!file.exists(), "File should not exist prior to test.");
        TradeList loadedList = storage.loadTrades();

        assertEquals(0, loadedList.size(), "Loading a non-existent file should return an empty list.");
    }

    /**
     * Tests the directory creation logic inside the saveTrades method.
     * Verifies that if the file path contains nested folders that don't exist,
     * the Storage class successfully creates the parent directories.
     *
     * @throws TradeLogException If directory creation or saving fails.
     */
    @Test
    public void saveTrades_nestedDirectory_createsParentDirectoriesSuccessfully() throws TradeLogException {
        String nestedFilePath = tempDir.resolve("data").resolve("saves").resolve("nested_log.txt").toString();
        Storage nestedStorage = new Storage(nestedFilePath);
        nestedStorage.setPassword("testpassword");
        TradeList emptyList = new TradeList();
        nestedStorage.saveTrades(emptyList);
        File nestedFile = new File(nestedFilePath);
        
        assertTrue(nestedFile.exists(), "The nested file should have been created.");
        assertTrue(nestedFile.getParentFile().exists(), "The parent directories should have been created.");
    }
}
