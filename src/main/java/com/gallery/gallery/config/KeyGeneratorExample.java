package com.gallery.gallery.config;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class KeyGeneratorExample {
    public static void main(String[] args) {
        try {
            // Initialize the KeyGenerator with the HmacSHA512 algorithm and key size (e.g., 512 bits)
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA512");
            keyGenerator.init(512);

            // Generate a secret key
            SecretKey secretKey = keyGenerator.generateKey();

            // Convert the secret key to a byte array (you may store this securely)
            byte[] keyBytes = secretKey.getEncoded();

            // Print or use the keyBytes as needed
            System.out.println("Generated Key: " + bytesToHex(keyBytes));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
}
