package com.brundhavanam.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JwtUtil is a helper class responsible for:
 * 1) Generating JWT tokens for authenticated users
 * 2) Validating tokens received from clients
 * 3) Extracting user identity (subject/mobile) from JWT token
 *
 * ✅ Uses Base64 encoded secret key and a secure HMAC SHA key (HS256).
 * ✅ Fixes weak secret key issues (must be >= 256 bits for HS256).
 */
@Component
public class JwtUtil {

    /**
     * JwtProperties contains JWT secret and expiration duration
     * loaded from application.yml / application.properties.
     */
    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Generates a signing key for HS256 algorithm.
     *
     * We store secret in Base64 form in configuration.
     * This method decodes the Base64 secret and converts it into a secure HMAC key.
     *
     * ✅ Keys.hmacShaKeyFor() ensures key is strong enough for HS256.
     */
    private Key getSigningKey() {

        // Decode BASE64 secret from application.yml
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());

        // Create secure signing key (prevents "key size not secure enough" error)
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for the given subject.
     *
     * In your project, subject = user's mobile number.
     *
     * Token includes:
     * - subject (mobile)
     * - issuedAt time
     * - expiry time
     * - signature (HS256)
     *
     * @param subject identity of user (mobile)
     * @return signed JWT token
     */
    public String generateToken(String subject) {

        Date now = new Date(); // current time
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration()); // expiry time

        return Jwts.builder()
                .setSubject(subject)                 // "sub" claim -> user identity (mobile)
                .setIssuedAt(now)                    // "iat" claim -> token issued time
                .setExpiration(expiryDate)           // "exp" claim -> token expiry time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // signature with secure key
                .compact();                          // final JWT string
    }

    /**
     * Extracts the subject (mobile) from token.
     *
     * @param token JWT token
     * @return subject stored in token ("sub")
     */
    public String extractSubject(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Validates token by checking:
     * ✅ token structure
     * ✅ signature correctness
     * ✅ expiration time
     *
     * If invalid token OR tampered token OR expired token, returns false.
     *
     * @param token JWT token
     * @return true if valid else false
     */
    public boolean validateToken(String token) {
        try {
            // If token is invalid, parsing will throw exception
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // JwtException -> malformed / expired / unsupported / invalid signature
            // IllegalArgumentException -> blank/empty token
            return false;
        }
    }

    /**
     * Parses and returns claims from a JWT token.
     *
     * Claims are the body/payload data inside JWT.
     * Example:
     * - sub = mobile
     * - iat = issue time
     * - exp = expiry time
     *
     * This method also verifies the token signature using the signing key.
     *
     * @param token JWT token
     * @return Claims payload
     */
    private Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // validates signature
                .build()
                .parseClaimsJws(token)          // parse & validate token
                .getBody();                     // return token body (claims)
    }
}
