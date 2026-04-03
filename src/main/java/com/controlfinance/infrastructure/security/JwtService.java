package com.controlfinance.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties props;

  private Key accessKey() {
    return Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  private Key refreshKey() {
    return Keys.hmacShaKeyFor(props.getRefreshSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(String userId, String email, String role) {
    Instant now = Instant.now();
    Instant exp = now.plus(props.getAccessTtlMinutes(), ChronoUnit.MINUTES);
    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .addClaims(Map.of(
            "email", email,
            "role", role,
            "typ", "access"
        ))
        .signWith(accessKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(String userId) {
    Instant now = Instant.now();
    Instant exp = now.plus(props.getRefreshTtlDays(), ChronoUnit.DAYS);
    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .addClaims(Map.of("typ", "refresh"))
        .signWith(refreshKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parseAccess(String token) {
    return Jwts.parserBuilder().setSigningKey(accessKey()).build().parseClaimsJws(token);
  }

  public Jws<Claims> parseRefresh(String token) {
    return Jwts.parserBuilder().setSigningKey(refreshKey()).build().parseClaimsJws(token);
  }
}
