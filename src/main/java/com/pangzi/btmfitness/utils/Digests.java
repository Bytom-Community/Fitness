package com.pangzi.btmfitness.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 张学文
 * @date 2018-03-22
 * @from copy basic-service common
 */
public class Digests {
    private static final String encodingCharset = "UTF-8";

    /**
     * SHA 摘要
     *
     * @param aValue
     * @return
     */
    public static String digest(String aValue, String algorithm) {
        aValue = aValue.trim();
        byte value[];
        try {
            value = aValue.getBytes(encodingCharset);
        } catch (UnsupportedEncodingException e) {
            value = aValue.getBytes();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return byte2hex(md.digest(value));
    }

    public static String digestSha256(String Value) {
        return digest(Value, "SHA-256");
    }


    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString();
    }
}
