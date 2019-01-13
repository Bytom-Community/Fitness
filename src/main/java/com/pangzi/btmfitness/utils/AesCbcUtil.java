package com.pangzi.btmfitness.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;

/**
 * @Author seven
 * @date 2018-02-26
 */
@Service
public class AesCbcUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    public String decrypt(String data, String key, String iv, String encodingFormat) {
        byte[] dataByte = Base64.decodeBase64(data);
        byte[] keyByte = Base64.decodeBase64(key);
        byte[] ivByte = Base64.decodeBase64(iv);
        try {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                Key spec = new SecretKeySpec(keyByte, "AES");
                AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
                parameters.init(new IvParameterSpec(ivByte));
                cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
                byte[] resultByte = cipher.doFinal(dataByte);
                if (null != resultByte && resultByte.length > 0) {
                    String result = new String(resultByte, encodingFormat);
                    return result;
                }
            } catch (NoSuchAlgorithmException e) {
                logger.error("[AesCbcUtil-decrypt]decode error:{}", e.getMessage());
            } catch (NoSuchPaddingException e) {
                logger.error("[AesCbcUtil-decrypt]decode error:{}", e.getMessage());
            } catch (InvalidParameterSpecException e) {
                logger.error("[AesCbcUtil-decrypt]decode error:{}", e.getMessage());
            } catch (InvalidAlgorithmParameterException e) {
                logger.error("[AesCbcUtil-decrypt]decode error:{}", e.getMessage());
            } catch (InvalidKeyException e) {
                logger.error("[AesCbcUtil-decrypt]decode error:{}", e.getMessage());
            } catch (BadPaddingException e) {
                logger.error("[AesCbcUtil-decrypt]decode error:{}", e.getMessage());
            } catch (IllegalBlockSizeException e) {
                logger.error("[AesCbcUtil-decrypt]decode error:{}", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                logger.error("[AesCbcUtil-decrypt]decode error:{}", e.getMessage());
            } finally {

            }
        } finally {

        }
        return null;
    }
}
