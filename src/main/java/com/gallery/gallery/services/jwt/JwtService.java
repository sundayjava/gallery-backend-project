package com.gallery.gallery.services.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${app.jwtKey}")
    private String jwtSecret;
    @Value("${app.jwtCookieName}")
    private String cookieName;

    public Cookie generateCookieFromJwt(String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(7 * 24 * 60 * 60 * 1000);
        return cookie;
    }

    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(cookieName, null).path("/").build();
    }

    public Claims extractAllClaims(HttpServletRequest request) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(getJwtFromCookie(request))
                .getPayload();
    }

    private Boolean isTokenExpired(HttpServletRequest request) {
        return extractExpiration(request).before(new Date());
    }

    public Boolean validateToken(HttpServletRequest request, UserDetails userDetails) {
        final String username = extractUsername(request);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(request));
    }

    public <T> T extractClaim(HttpServletRequest request, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(request);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(HttpServletRequest request) {
        return extractClaim(request, Claims::getSubject);
    }

    public String extractId(HttpServletRequest request) {
        return extractClaim(request, Claims::getId);
    }

    public Date extractExpiration(HttpServletRequest request) {
        return extractClaim(request, Claims::getExpiration);
    }

    public String generateToken(String email, UUID id) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, id);
    }

    private String createToken(Map<String, Object> claims, String email, UUID id) {
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .id("" + id)
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .signWith(getSignKey(), Jwts.SIG.HS512)
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
