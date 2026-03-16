package com.app.cms.common.security;

import com.app.cms.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Getter
@Setter
@RequiredArgsConstructor
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${security.jwt.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;

    private final RedisTemplate<String, String> template;
    private final String ACTIVE = "refresh:active";

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public Long extractOrganizationId(String token) {
        return extractClaim(token, claims -> claims.get("organizationId", Long.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "ACCESS");

        if (userDetails instanceof UserDetailsImpl userDetailsImpl) {
            User user = userDetailsImpl.getUser();
            claims.put("userId", user.getId());
            claims.put("organizationId", user.getOrganization().getId());
            claims.put("role", user.getRole().toString());
            claims.put("firstName", user.getFirstName());
            claims.put("lastName", user.getLastName());
        }

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .collect(Collectors.toSet());
        claims.put("roles", roles);

        Set<String> permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith("ROLE_"))
                .collect(Collectors.toSet());

        claims.put("permissions", permissions);

        return buildToken(claims, userDetails.getUsername(), accessTokenExpirationTime);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");

        if (userDetails instanceof UserDetailsImpl userDetailsImpl) {
            User user = userDetailsImpl.getUser();
            claims.put("userId", user.getId());
        }

        String activeKey = ACTIVE + userDetails.getUsername();
        return buildToken(claims, userDetails.getUsername(), refreshTokenExpirationTime);
    }

    public Map<String, String> generateTokenPair(UserDetails userDetails) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateAccessToken(userDetails));
        tokens.put("refreshToken", generateRefreshToken(userDetails));

        return tokens;
    }

    public Map<String, String> refreshTokens(String refreshToken, UserDetails userDetails) {
        String tokenType = extractTokenType(refreshToken);

        if (!"REFRESH".equals(tokenType)) {
            throw new IllegalArgumentException("invalid token type, expects refresh");
        }

        if (!isTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("invalid or expired token");
        }
        return generateTokenPair(userDetails);
    }

    private String buildToken(
            Map<String, Object> claims,
            String userName,
            long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public Long getAccessTokenExpirationTime() {
        return accessTokenExpirationTime / 1000;
    }
}
