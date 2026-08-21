package com.paypal.user_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {

    // 1. MUST be at least 256 bits (32 characters/bytes) for HS256 security compliance
    private static final String SECRET = "12345";

//    private Key getSigningKey() {
//        return Keys.hmacShaKeyFor(SECRET.getBytes());
//    }

private javax.crypto.SecretKey getSigningKey() {
    return io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET.getBytes());
}


    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 2. Actually matches the extracted email against the requested username
    public boolean validateToken(String token, String username) {
        try {
            String email = extractEmail(token);
            return (email != null && email.equals(username));
        } catch (Exception e) {
            return false;
        }
    }

    // 3. Pointed to extractEmail to eliminate redundant parsing logic
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(getSigningKey())
                .compact();
    }
}
