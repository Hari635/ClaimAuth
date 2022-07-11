package com.cts.claimauth.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.cts.claimauth.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
   @Value("${cts.app.jwtSecret}")
   private String jwtSecret;
   
   @Value("${cts.app.jwtExpirationMs}")
   private int jwtExpirationMs;
   
   public String generateJwtToken(UserDetailsImpl userPrincipal) {
	   logger.info("inside the the jwtutil");
	   return generateTokenFromUserId(userPrincipal.getUserid());
	   
   }
   
   public String generateTokenFromUserId(Long userId ) {
	   return Jwts.builder().setSubject(userId.toString()).setIssuedAt(new Date())
			   .setExpiration(new Date((new Date()).getTime()+jwtExpirationMs)).signWith(SignatureAlgorithm.HS512,jwtSecret)
			   .compact();
   }
   
   public String getUserIdFromJwtToken(String token) {
	   return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
   }
   
   public boolean validateJwtToken(String authToken) {
	   try {
		      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
		      return true;
		    } catch (SignatureException e) {
		      logger.error("Invalid JWT signature: {}", e.getMessage());
		    } catch (MalformedJwtException e) {
		      logger.error("Invalid JWT token: {}", e.getMessage());
		    } catch (ExpiredJwtException e) {
		      logger.error("JWT token is expired: {}", e.getMessage());
		    } catch (UnsupportedJwtException e) {
		      logger.error("JWT token is unsupported: {}", e.getMessage());
		    } catch (IllegalArgumentException e) {
		      logger.error("JWT claims string is empty: {}", e.getMessage());
		    }

		    return false;
   }
   
   
}
