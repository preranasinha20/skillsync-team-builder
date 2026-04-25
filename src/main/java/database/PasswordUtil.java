package database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    // Private constructor — this is a utility class, never instantiate it
    private PasswordUtil() {}

    /**
     * Hashes a plain text password using SHA-256.
     * Call this BEFORE registering or logging in.
     *
     * Usage:
     *   String hash = PasswordUtil.hash("mypassword123");
     */
    public static String hash(String plainTextPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(plainTextPassword.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verifies a plain text password against a stored hash.
     * Usage:
     *   boolean valid = PasswordUtil.verify("mypassword123", storedHash);
     */
    public static boolean verify(String plainTextPassword, String storedHash) {
        return hash(plainTextPassword).equals(storedHash);
    }
}