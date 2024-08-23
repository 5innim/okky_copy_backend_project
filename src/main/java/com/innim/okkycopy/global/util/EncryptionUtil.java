package com.innim.okkycopy.global.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtil {

    public static String encryptWithSHA256(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();

        return byteToHexString(digest.digest(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static String connectStrings(String a, String b, String divider) {
        StringBuilder sb = new StringBuilder(a);
        sb.append(divider);
        sb.append(b);

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

    public static String base64Decode(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }

}
