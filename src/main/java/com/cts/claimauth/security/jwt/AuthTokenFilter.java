package com.cts.claimauth.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.cts.claimauth.security.services.UserDetailServiceImpl;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class AuthTokenFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private UserDetailServiceImpl userDetailsService;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		logger.info("inside filerter---------");
		try {
			String jwt=parseJwt(request);
			if(jwt!=null && jwtUtils.validateJwtToken(jwt)) {
				String userid=jwtUtils.getUserIdFromJwtToken(jwt);
				
				UserDetails userDetails=userDetailsService.loadUserByUsername(userid);
				
				 UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				            userDetails.getAuthorities());
				        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				        SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}catch(Exception e) {
			logger.error("Cannot set User authentication: {}",e.getMessage());
		}
		
		filterChain.doFilter(request, response);

	}
	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

	    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
	      return headerAuth.substring(7, headerAuth.length());
	    }

	    return null;
	}

}
