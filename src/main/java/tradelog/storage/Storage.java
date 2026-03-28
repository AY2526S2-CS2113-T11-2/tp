package tradelog.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;

/**
 * Handles reading and writing of trade data to and from a file in an encrypted format.
 */
public class Storage {

    private static final String ALGORITHM = "AES";
    private SecretKeySpec secretKey;
    private String passwordHash;

    /** Path to the file used for persistent storage. */
    private final String filePath;

    /**
     * Constructs a Storage instance with the specified file path.
     *
     * @param filePath Path to the storage file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Checks if the storage file exists.
     *
     * @return true if the file exists, false otherwise.
     */
    public boolean exists() {
        return new File(filePath).exists();
    }

    /**
     * Sets the password to be used for encryption and decryption.
     *
     * @param password The password provided by the user.
     * @throws TradeLogException If the hashing algorithm is not found.
     */
    public void setPassword(String password) throws TradeLogException {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(password.getBytes(StandardCharsets.UTF_8));
            this.passwordHash = Base64.getEncoder().encodeToString(key);
            key = Arrays.copyOf(key, 16); // use first 16 bytes for AES-128
            this.secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new TradeLogException("Encryption error: SHA-256 algorithm not found.");
        }
    }

    /**
     * Encrypts the given plaintext string using AES.
     *
     * @param data The plaintext string to encrypt.
     * @return The Base64 encoded ciphertext.
     * @throws Exception If encryption fails.
     */
    private String encrypt(String data) throws Exception {
        if (secretKey == null) {
            throw new TradeLogException("Password not set.");
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts the given ciphertext string using AES.
     *
     * @param encryptedData The Base64 encoded ciphertext to decrypt.
     * @return The decrypted plaintext string.
     * @throws Exception If decryption fails.
     */
    private String decrypt(String encryptedData) throws Exception {
        if (secretKey == null) {
            throw new TradeLogException("Password not set.");
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Saves the given TradeList to the storage file in an encrypted format.
     *
     * @param tradeList The list of trades to save.
     * @throws TradeLogException If the file cannot be written or encryption fails.
     */
    public void saveTrades(TradeList tradeList) throws TradeLogException {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                boolean isDirCreated = file.getParentFile().mkdirs();
                if (!isDirCreated) {
                    throw new TradeLogException("Failed to create directory: "
                            + file.getParentFile().getPath());
                }
            }
            try (FileWriter writer = new FileWriter(filePath)) {
                // Write password hash on the first line
                writer.write(passwordHash);
                writer.write("\n");
                for (int i = 0; i < tradeList.size(); i++) {
                    String encryptedLine = encrypt(tradeList.getTrade(i).toStorageString());
                    writer.write(encryptedLine);
                    writer.write("\n");
                }
            }
        } catch (Exception e) {
            throw new TradeLogException("Failed to save trades: " + e.getMessage());
        }
    }

    /**
     * Loads trades from the storage file and returns them as a TradeList.
     *
     * @return A TradeList containing all trades loaded from the file.
     * @throws TradeLogException If the file cannot be read or decryption fails.
     */
    public TradeList loadTrades() throws TradeLogException {
        TradeList tradeList = new TradeList();
        File file = new File(filePath);

        if (!file.exists()) {
            return tradeList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Read first line and verify password hash
            String storedHash = reader.readLine();
            if (storedHash == null || !storedHash.equals(passwordHash)) {
                throw new TradeLogException("Incorrect password or corrupted file.");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String decryptedLine = decrypt(line);
                    String[] parts = decryptedLine.split(" \\| ");
                    if (parts.length == 8) {
                        String ticker = parts[0];
                        String date = parts[1];
                        String direction = parts[2];
                        double entryPrice = Double.parseDouble(parts[3]);
                        double exitPrice = Double.parseDouble(parts[4]);
                        double stopLossPrice = Double.parseDouble(parts[5]);
                        String outcome = parts[6];
                        String strategy = parts[7];
                        tradeList.addTrade(new Trade(ticker, date, direction,
                                entryPrice, exitPrice, stopLossPrice, outcome, strategy));
                    }
                } catch (Exception e) {
                    throw new TradeLogException("Failed to decrypt trade data. "
                            + "The file might be corrupted or incorrect password.");
                }
            }
        } catch (IOException e) {
            throw new TradeLogException("Failed to load trades");
        }

        return tradeList;
    }
}
