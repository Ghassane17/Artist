package Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    private static final String SECRET_KEY = "my-super-secure-secret-key-for-jwt-signing-12345";
    private static final long EXPIRATION_MS = 15 * 60 * 1000; // 15 minutes

       private SecretKey key() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username , String email) {
        return Jwts.builder()
                .subject(email)  // Store email as subject
                .issuedAt(new Date())                  // .setIssuedAt() → .issuedAt()
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS)) // .setExpiration() → .expiration()
                .signWith(key())  // Now key() returns SecretKey
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key())  // Now works with SecretKey
                .build()
                .parseSignedClaims(token)               // .parseClaimsJws() → .parseSignedClaims()
                .getPayload()                           // .getBody() → .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromJwtToken(String token) {
    return Jwts.parser()
            .verifyWith(key())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();  // The subject IS the email
}
}