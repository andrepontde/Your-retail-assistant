package dev.andrepontde.retailmanager.retail_system.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * JWT utility class for token generation, validation, and extraction.
 * Handles all JWT operations for user authentication.
 */
@Component
public class JwtUtil {

    // Default secret key - in production, use application.properties
    @Value("${jwt.secret:mySecretKey12345678901234567890123456789012345678901234567890}")
    private String secretKey;

    @Value("${jwt.expiration:86400}") // 24 hours in seconds
    private int jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Generate JWT token for a user.
     * Includes user info and store context.
     */
    public String generateToken(Long userId, String username, String role, Long storeId, String storeName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("storeId", storeId);
        claims.put("storeName", storeName);
        
        return createToken(claims, username);
    }

    /**
     * Create JWT token with claims.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000L);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username from JWT token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from JWT token.
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Extract user role from JWT token.
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extract store ID from JWT token.
     */
    public Long extractStoreId(String token) {
        return extractClaim(token, claims -> claims.get("storeId", Long.class));
    }

    /**
     * Extract store name from JWT token.
     */
    public String extractStoreName(String token) {
        return extractClaim(token, claims -> claims.get("storeName", String.class));
    }

    /**
     * Extract expiration date from JWT token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from JWT token.
     */
    public <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    /**
     * Extract all claims from JWT token.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * Check if JWT token is expired.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate JWT token.
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Validate JWT token without username check.
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get token expiration time in seconds.
     */
    public int getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Functional interface for claims resolution.
     */
    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}
