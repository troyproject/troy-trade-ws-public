package com.troy.streamingexchange;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;

/**
 * CommonUtil
 *
 * @author liuxiaocheng
 * @date 2018/6/27
 */
public final class CommonUtil {
    private final static String HMAC_SHA_512 = "HmacSHA512";
    private final static String CHARSET_UTF_8 = "UTF-8";
    private static Mac mac;

    public static Long getNonce() {
        Calendar calendar = Calendar.getInstance();
        Long number = calendar.getTime().getTime();
        return number;
    }

    public static String getSignature(String secretKeyBase64, Long nonce) {
        try {
            SecretKey secretKey = new SecretKeySpec(secretKeyBase64.getBytes(CHARSET_UTF_8), HMAC_SHA_512);
            mac = Mac.getInstance(HMAC_SHA_512);
            mac.init(secretKey);
            mac.update(nonce.toString().getBytes(CHARSET_UTF_8));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not decode Base 64 string", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("Invalid key for hmac initialization.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Illegal algorithm for post body digest. Check the implementation.");
        }
        return Base64.getEncoder().encodeToString(mac.doFinal()).trim();
    }

    public static void main(String[] args) {
        String expect = "Uy4ln2O36RwRO3IDmC3hKY8GP6BkT+8uslicye/ANSlLxBzr1wJ/zoSbYvRqxvgnXXRyoAGjbOZS/HiXz2KTRQ==";
        String sign = getSignature("a91f9b51f5772ee3a36b39695b503d20ee5f64fefccf468db8e060636a05cea4", 1530509980521L);
        System.out.println("expect:" + expect);
        System.out.println("  sign:" + sign);
        System.out.println(expect.equalsIgnoreCase(sign));
    }
}
