package com.burnoutinhos.burnoutinhos_api.config;

import com.burnoutinhos.burnoutinhos_api.exceptions.TokenValidationException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    private final SecretKey privateKey = Keys.secretKeyFor(
        SignatureAlgorithm.HS256
    );

    public String buildToken(String username) {
        Date actualDate = new Date();

        JwtBuilder builder = Jwts.builder()
            .subject(username)
            .issuedAt(actualDate)
            .expiration(new Date(actualDate.getTime() + (1209600000L)))
            .signWith(privateKey);
        return builder.compact();
    }

    public String extractUsername(String token) {
        JwtParser parser = Jwts.parser().verifyWith(privateKey).build();
        return parser.parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(privateKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            throw new TokenValidationException("Token inválido");
        } catch (IllegalArgumentException e) {
            throw new TokenValidationException("Token mal formado");
        }
    }
}
