package com.auth.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RSAPrivateKey rsaPrivateKey;
    private final RSAPublicKey rsaPublicKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // CREATE JWT (SIGN WITH PRIVATE KEY)
    public String generateToken(Long id, String userType, Set<String> permissions) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("userType", userType)
                .claim("permissions", permissions)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(rsaPrivateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    // VALIDATE JWT (VERIFY WITH PUBLIC KEY)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(rsaPublicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // READ CLAIMS
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(rsaPublicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public String getUserType(String token) {
        return getClaims(token).get("userType", String.class);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getPermissions(String token) {

        Object permissionsObj = getClaims(token).get("permissions");

        if (permissionsObj == null) {
            return Set.of();
        }

        return ((List<?>) permissionsObj)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}
