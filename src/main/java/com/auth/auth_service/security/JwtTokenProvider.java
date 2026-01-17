package com.auth.auth_service.security;

import com.auth.auth_service.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    private Key getSigningKey()
    {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    //Create JWT
    public String generateToken(Long id, String userType, Set<String> permission)
    {
        Date date = new Date();
        Date expiry = new Date(date.getTime() + jwtConfig.getExpirationMs());

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("userType", userType)
                .claim("permissions", permission)
                .setIssuedAt(date)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //validate jwt
    public boolean validateToken(String token)
    {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // READ CLAIMS
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
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
