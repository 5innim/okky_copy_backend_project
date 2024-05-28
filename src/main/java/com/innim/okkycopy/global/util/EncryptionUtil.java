package com.innim.okkycopy.global.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtil {

    private final static String DIVIDER_STRING = "/";

    public static String encryptWithSHA256(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();

        return byteToHexString(digest.digest(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static String connectStrings(String... str) {
        StringBuilder sb = new StringBuilder(str[0]);
        for (int i=1; i < str.length; i++) {
            sb.append('/');
            sb.append(str[i]);
        }
        return sb.toString();
    }

    public static String base64Encode(String key) throws NoSuchAlgorithmException {
        return new String(Base64.getEncoder().encode(key.getBytes(StandardCharsets.UTF_8)));
    }

    public static String byteToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
