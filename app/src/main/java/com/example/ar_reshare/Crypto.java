package com.example.ar_reshare;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Pair;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class Crypto {
    private final static String CIPHER = "AES/GCM/NoPadding";

    private Crypto() {}

    // Given a password, return the encrypted password and initialisation vector as a pair,
    // while storing the key used for encryption securely
    public static Pair<byte[], byte[]> encrypt(String password) {
        try {
            final KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder("passwordKey",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();

            keyGenerator.init(keyGenParameterSpec);
            final SecretKey secretKey = keyGenerator.generateKey();

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] ciphertext = cipher.doFinal(password.getBytes());
            byte[] iv = cipher.getIV();

            Pair<byte[], byte[]> encryptedPair = new Pair<>(ciphertext, iv);
            return encryptedPair;
        } catch (Exception e) {
            return null;
        }
    }

    // Given the encrypted password and initialisation vector as a pair,
    // decrypt the ciphertext and return as a String
    public static String decrypt(byte[] ciphertext, byte[] iv) {
        try {
            final SecretKey secretKey = getKeyFromKeyStore();

            final Cipher cipher = Cipher.getInstance(CIPHER);
            final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            final byte[] decryptedData = cipher.doFinal(ciphertext);

            return new String(decryptedData, "UTF-8");
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    // Get the key used for encryption
    private static SecretKey getKeyFromKeyStore() {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null, null);

            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) ks
                    .getEntry("passwordKey", null);

            final SecretKey secretKey = secretKeyEntry.getSecretKey();
            return secretKey;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // Delete the key with the given alias
    private static boolean removeKey(String keyAlias) {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null, null);

            if (ks.isKeyEntry(keyAlias)) {
                ks.deleteEntry(keyAlias);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

}

