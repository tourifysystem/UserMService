
// package bt.edu.gcit.usermicroservice.security;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.stereotype.Component;

// import javax.crypto.SecretKey;
// import java.util.Collection;
// import java.util.Date;

// @Component
// public class JwtUtil {

//     private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//     private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour in milliseconds

//     public String generateToken(String username) {
//         return Jwts.builder()
//                 .setSubject(username)
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                 .signWith(SECRET_KEY)
//                 .compact();
//     }

//     public String extractUsername(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(SECRET_KEY)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody()
//                 .getSubject();
//     }

//     public boolean isTokenExpired(String token) {
//         return extractExpirationDate(token).before(new Date());
//     }

//     private Date extractExpirationDate(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(SECRET_KEY)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody()
//                 .getExpiration();
//     }

//     public boolean validateToken(String token, String username) {
//         return (username.equals(extractUsername(token)) && !isTokenExpired(token));
//     }
// }
package bt.edu.gcit.usermicroservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username (subject) from token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    // Extract expiration date
    private Date extractExpirationDate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // Validate token
    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }
}
