package com.pangzi.btmfitness.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @author 张学文
 * @date 2018-03-22
 */
public class JwtUtils {

    public static final String USER_NAME = "openId";
    private final static SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    private final static long JWT_EXPIRED_TIME = 2419200000L;
    private final static String ENCODED_KEY = "MawprKgCTVtm8Ve5SbDsV9J5z7RMCP6h97bmXhm5Gu7PgEHgVAP7yzBrG5aH";
    private final static Key SECRET_KEY = deserializeKey(ENCODED_KEY);

    private static Key deserializeKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, SIGNATURE_ALGORITHM.getJcaName());
    }

    /**
     * 创建token
     *
     * @param map
     * @param jwtExpiredTime
     * @return
     */
    public static String createJWT(Map<String, Object> map, Long jwtExpiredTime) {
        jwtExpiredTime = jwtExpiredTime != null ? jwtExpiredTime : JWT_EXPIRED_TIME;
        String token = Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiredTime))
                .signWith(SIGNATURE_ALGORITHM, ENCODED_KEY)
                .compact();
        return token;
    }

    public static boolean decodeJWT(String token, String openId) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            if (openId.equals(claims.get(USER_NAME))) {
                return true;
            }
        } catch (Exception e) {
            System.out.println((("decodeJWT fail : " + e.getMessage())));
        }
        return false;
    }
}
