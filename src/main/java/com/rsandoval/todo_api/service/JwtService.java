package com.rsandoval.todo_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String JWT_SECRET = loadSecretKey();

    // Main method we will call when a user logs in
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // Add any custom data (optional)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // "Created at" timestamp
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Expires in 24 hours
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // "Stamp" it with our secret key
                .compact();
    }

    // Helper method to decode secret key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract "Digital Signature" key from config.properties
    private static String loadSecretKey(){
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            String key = prop.getProperty("JWT_SECRET");
            if (key == null || key.trim().isEmpty()) throw new RuntimeException("JWT_SECRET not found in config.properties");
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    // READ TOKEN
    // Extract the username (Subject) from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract any specific data (claim)
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Reads the encrypted token using the Secret Key
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // VALIDATE TOKEN
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


}
