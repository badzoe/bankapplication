package com.bankapp.bankapplication.config;

import com.bankapp.bankapplication.repository.UserRepository;
import com.bankapp.bankapplication.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration}")
    private Long jwtExpirationDate;

    @Autowired
    private UserRepository userRepository;

    public String generateToken(Authentication authentication){

        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        Map<String, Object> claims = new HashMap<>();

        log.info(username);

        claims.put("userID",userRepository.findByEmail(username).get().getId());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .claim("userID", userRepository.findByEmail(username).get().getId())
                .signWith(key())
                .compact();
    }

    private Key key(){
        byte[] bytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String getUsername(String token){
        return extract(token).getSubject();
    }

    public boolean validateToken(String token){

        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (ExpiredJwtException | IllegalArgumentException | SecurityException | MalformedJwtException e) {
            throw new RuntimeException(e);
        }
    }

    public Date getExpirationDate(String token) {
        return extract(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    private Claims extract (String token){
        return Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        return extract(token).get("userID", Long.class);
    }

}