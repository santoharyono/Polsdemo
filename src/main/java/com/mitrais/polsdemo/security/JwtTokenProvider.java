package com.mitrais.polsdemo.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtsecret}")
    private String jwtSecret;

    @Value("${app.jwtexpiration}")
    private int jwtExpiration;

    public String generateToken(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Long getUserIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);

            return true;
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT Token");
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Invalid JWT Token");
        }catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT Token");
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty");
        }

        return false;
    }
}
