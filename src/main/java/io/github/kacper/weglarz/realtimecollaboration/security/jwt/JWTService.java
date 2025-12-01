package io.github.kacper.weglarz.realtimecollaboration.security.jwt;

import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Service responsible for JWT Session Token
 */
@Service
public class JWTService {

    /**
     * Secret key from application.properties
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Expiration time from application.properties
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generate sessionToken
     * @param user takes username and email
     * @return token coded with HS256
     */
    public String generateToken(User user) {

        return Jwts.builder()
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();

    }

    /**
     * Get secretKey and decoded to keyBytes
     * @return secret key
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Checks if the given JWT token is valid
     * @param token JWT token to verify
     * @return true if the token is valid
     */
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reads the username stored inside the JWT token
     * @param token JWT token
     * @return username from the token
     */
    public String getUsername(String token) {
        return getClaims(token).get("username").toString();
    }

    /**
     * Reads and returns Claims object from the JWT token
     * @param token JWT token
     * @return Claims containing token data
     */
    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getBody();
    }
}
