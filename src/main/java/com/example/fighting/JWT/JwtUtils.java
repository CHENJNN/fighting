package com.example.fighting.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(String userName) {
        Claims claims = Jwts.claims().setSubject(userName);
//        claims.put("auth", role);
        Date validity = new Date(new Date().getTime() + 432000000L);
        String token = Jwts.builder()
                .signWith(key)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .compact();
        return token;
    }

    public static boolean validateToken(String token) {
        try {
            getTokenBody(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Request to parse expired JWT : {} failed : {}", token, e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("Request to parse unsupported JWT : {} failed : {}", token, e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("Request to parse invalid JWT : {} failed : {}", token, e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Request to parse empty or null JWT : {} failed : {}", token, e.getMessage());
        }
        return false;
    }

    public static Authentication getAuthentication(String token) {
        Claims claims = getTokenBody(token);
        String roles = (String)claims.get("auth");
        String userName = claims.getSubject();
        return new UsernamePasswordAuthenticationToken(userName, token, AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
    }

    public static Claims getTokenBody(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

}
