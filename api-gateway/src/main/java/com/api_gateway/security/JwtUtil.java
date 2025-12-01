package com.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    // In real app, use config/secret manager.
    private static final String SECRET = "my-super-secret-key-my-super-secret-key";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public Claims validateTokenAndGetClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String getUsername(String token) {
        return validateTokenAndGetClaims(token).getSubject();
    }

    public String getRole(String token) {
        Object role = validateTokenAndGetClaims(token).get("role");
        return role != null ? role.toString() : null;
    }
}
