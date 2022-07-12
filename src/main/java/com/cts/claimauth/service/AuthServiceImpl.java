package com.cts.claimauth.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cts.claimauth.controller.AuthController;
import com.cts.claimauth.exception.TokenException;
import com.cts.claimauth.models.Role;
import com.cts.claimauth.models.URole;
import com.cts.claimauth.models.User;
import com.cts.claimauth.payload.response.JwtResponse;
import com.cts.claimauth.payload.request.SignupRequest;
import com.cts.claimauth.payload.response.TokenValidation;
import com.cts.claimauth.repository.RoleRepository;
import com.cts.claimauth.repository.UserRepository;
import com.cts.claimauth.security.jwt.JwtUtils;
import com.cts.claimauth.security.services.UserDetailServiceImpl;
import com.cts.claimauth.security.services.UserDetailsImpl;

@Service
public class AuthServiceImpl implements AuthService {
	private static final Logger logger=LoggerFactory.getLogger(AuthServiceImpl.class);
	@Autowired
	  AuthenticationManager authenticationManager;
	  
	  @Autowired
	  UserRepository userRepository;
	  
	  @Autowired
	  RoleRepository roleRepository;
	  
	  @Autowired
	  PasswordEncoder encoder;
	  
	  @Autowired
	  JwtUtils jwtUtils;
	  
	  @Autowired
	  UserDetailServiceImpl userDetailsService;
	  
	  public JwtResponse signInResponse(String userId,String password) {
		  
		  Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId,password));
		  SecurityContextHolder.getContext().setAuthentication(authentication);

		    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		    String jwt = jwtUtils.generateJwtToken(userDetails);
		    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
		            .collect(Collectors.toList());
		    Optional<User> optionalUser=userRepository.findByUserId(userDetails.getUserid());
		    User user=optionalUser.get();
		    
		    return new JwtResponse(jwt,userDetails.getUserid(),
		            user.getName(), userDetails.getEmail(), roles);
	  }
	  
	  public boolean existPhoneNo(String phoneNo) {
		 if( userRepository.existsByPhoneNo(phoneNo)) {
			 return true;
		 }else {
			 return false;
		 }
	  }
	  public boolean existEmail(String email) {
		  if(userRepository.existsByEmail(email)) {
			  return true;
		  }else {
			  return false;
		  }
	  }
	  public JwtResponse signupResponse(String name,String email,String password,String phoneNo,String address) {
		  User user = new User(name,email,
		            encoder.encode(password),phoneNo,address);
		    
		    Set<Role> roles = new HashSet<>();
		    
		    Role userRole = roleRepository.findByName(URole.ROLE_USER)
		            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		        roles.add(userRole); 
		        
		   user.setRoles(roles);
		   userRepository.save(user);
		   List<String> roleslist = roles.stream().map(item->item.getName().toString()).collect(Collectors.toList());
		   
		   String jwt = jwtUtils.generateTokenFromUserId(user.getUserId());
		   
		   return (new JwtResponse(jwt,user.getUserId(),
		            user.getName(), user.getEmail(),roleslist));
	  }

	public JwtResponse adminSignUp(String name, String email, String password, String phoneNo, String address) {
		User user = new User(name,email,
	            encoder.encode(password),phoneNo,address);
	    
	    Set<Role> roles = new HashSet<>();
	    
	    Role userRole = roleRepository.findByName(URole.ROLE_ADMIN)
	            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	        roles.add(userRole); 
	        
	   user.setRoles(roles);
	   userRepository.save(user);
	   List<String> roleslist = roles.stream().map(item->item.getName().toString()).collect(Collectors.toList());
	   
	   String jwt = jwtUtils.generateTokenFromUserId(user.getUserId());
	   
	   return (new JwtResponse(jwt,user.getUserId(),
	            user.getName(), user.getEmail(),roleslist));
	}
	public TokenValidation validationToken(String tokenDup) {
		String token = tokenDup.substring(7);		
		try {
			if (Boolean.TRUE.equals(jwtUtils.validateJwtToken(token))) {
				return new TokenValidation(true);
			} else {
				throw new TokenException(token,"Invalid Token");
			}
		} catch (Exception e) {
			
			return new TokenValidation(false);
		}
	}
	
	public User searchUser(String id,SignupRequest signUpRequest) {
		Long useId=Long.parseLong(id);
		Optional<User> optionalUser=userRepository.findByUserId(useId);
		User user=optionalUser.get();
		if(signUpRequest.getEmail()!=null) {
			user.setEmail(signUpRequest.getEmail());
		}
		if(signUpRequest.getName()!=null) {
			user.setName(signUpRequest.getName());
		}
		if(signUpRequest.getPhoneNo()!=null) {
			user.setPhoneNo(signUpRequest.getPhoneNo());
		}
		if(signUpRequest.getAddress()!=null) {
			user.setAddress(signUpRequest.getAddress());
		}
		if(signUpRequest.getPassword()!=null) {
			user.setPassword(encoder.encode(signUpRequest.getPassword()));
		}
		userRepository.save(user);
		
		return user;
//		return new JwtResponse(null,user.getUserId(),user.getName(),user.getEmail(),null);
		
	}
	
	public User userCheck(String id) {
		  Optional<User> findByUserId = userRepository.findByUserId(Long.parseLong(id));
		  User user=findByUserId.get();
		  return user;
	}
}
