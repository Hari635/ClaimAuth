package com.cts.claimauth.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.cts.claimauth.exception.TokenException;
import com.cts.claimauth.models.Role;
import com.cts.claimauth.models.URole;
import com.cts.claimauth.models.User;
import com.cts.claimauth.payload.request.LoginRequest;
import com.cts.claimauth.payload.request.SignupRequest;
import com.cts.claimauth.payload.response.JwtResponse;
import com.cts.claimauth.payload.response.MessageResponse;
import com.cts.claimauth.payload.response.TokenValidation;
import com.cts.claimauth.repository.RoleRepository;
import com.cts.claimauth.repository.UserRepository;
import com.cts.claimauth.security.jwt.AuthEntryPointJwt;
import com.cts.claimauth.security.jwt.JwtUtils;
import com.cts.claimauth.security.services.UserDetailServiceImpl;
import com.cts.claimauth.security.services.UserDetailsImpl;

@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE} )
@RequestMapping("api/auth")
public class AuthController {
	private static final Logger logger=LoggerFactory.getLogger(AuthController.class);
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
  
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
	  logger.info("inside the signin---%%%------");
	  logger.info("inside the signin---%%%------");
	  logger.info(loginRequest.getUserid().toString());
	  
	  Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserid().toString(),loginRequest.getPassword()));
	  logger.info("inside the signinout---%%%------");
	  SecurityContextHolder.getContext().setAuthentication(authentication);
	  logger.info("inside the signinouted---%%%------");

	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	    logger.info(userDetails.getUserid().toString());

	    String jwt = jwtUtils.generateJwtToken(userDetails);
	    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
	            .collect(Collectors.toList());
	    Optional<User> optionalUser=userRepository.findByUserId(userDetails.getUserid());
	    User user=optionalUser.get();
	    
	    return ResponseEntity.ok(new JwtResponse(jwt,userDetails.getUserid(),
	            user.getName(), userDetails.getEmail(), roles));
  }
  
  
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
	  
	  if (userRepository.existsByPhoneNo(signUpRequest.getPhoneNo())) {
	      return ResponseEntity.badRequest().body(new MessageResponse("Error: PhoneNo is already taken!"));
	    }

	    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
	      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
	    }
	    
	    
	    User user = new User(signUpRequest.getName(), signUpRequest.getEmail(),
	            encoder.encode(signUpRequest.getPassword()),signUpRequest.getPhoneNo(),signUpRequest.getAddress());
	    
	    Set<Role> roles = new HashSet<>();
	    
	    Role userRole = roleRepository.findByName(URole.ROLE_USER)
	            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	        roles.add(userRole); 
	        
	   user.setRoles(roles);
	   userRepository.save(user);
	   List<String> roleslist = roles.stream().map(item->item.getName().toString()).collect(Collectors.toList());
	   
	   String jwt = jwtUtils.generateTokenFromUserId(user.getUserId());
	   
	   return ResponseEntity.ok(new JwtResponse(jwt,user.getUserId(),
	            user.getName(), user.getEmail(),roleslist));
  }
  
  @PostMapping("/adminsignup")
  public ResponseEntity<?> registerAdminUser(@Valid @RequestBody SignupRequest signUpRequest) {
	  
	  if (userRepository.existsByPhoneNo(signUpRequest.getPhoneNo())) {
	      return ResponseEntity.badRequest().body(new MessageResponse("Error: PhoneNo is already taken!"));
	    }

	    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
	      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
	    }
	    
	    
	    User user = new User(signUpRequest.getName(), signUpRequest.getEmail(),
	            encoder.encode(signUpRequest.getPassword()),signUpRequest.getPhoneNo(),signUpRequest.getAddress());
	    
	    Set<Role> roles = new HashSet<>();
	    
	    Role userRole = roleRepository.findByName(URole.ROLE_ADMIN)
	            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	        roles.add(userRole); 
	        
	   user.setRoles(roles);
	   userRepository.save(user);
	   List<String> roleslist = roles.stream().map(item->item.getName().toString()).collect(Collectors.toList());
	   
	   String jwt = jwtUtils.generateTokenFromUserId(user.getUserId());
	   
	   return ResponseEntity.ok(new JwtResponse(jwt,user.getUserId(),
	            user.getName(), user.getEmail(),roleslist));
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<?>  getUser(@PathVariable Long id){
	  Optional<User> user=userRepository.findById(id);
	  return ResponseEntity.ok(user);
  }
  
  @GetMapping(path = "/validate-v1")
   public ResponseEntity<?> validatingAuthorizationToken( @RequestHeader(name = "Authorization") String tokenDup) {
		
		logger.info("BEGIN - [validatingAuthorizationToken(JWT-token)]");
		String token = tokenDup.substring(7);
		logger.info(token);		
		try {
			if (Boolean.TRUE.equals(jwtUtils.validateJwtToken(token))) {
				logger.debug("Token matched is Valid");
				logger.info("Token matched is Valid");
				logger.info("END - validate()");
				return new ResponseEntity<>(new TokenValidation(true), HttpStatus.OK);
			} else {
				throw new TokenException(token,"Invalid Token");
			}
		} catch (Exception e) {
			logger.debug("Invalid token - Bad Credentials Exception");
			logger.info("END Exception - validatingAuthorizationToken()");
			
			return new ResponseEntity<>(new TokenValidation(false), HttpStatus.BAD_REQUEST);
		}
		
	}
  @GetMapping(path = "/validate/{tokenDup}")
  public ResponseEntity<?> validatingAuthorizationTokenService( @PathVariable String tokenDup) {
		
		logger.info("BEGIN - [validatingAuthorizationToken(JWT-token)]");
		String token = tokenDup.substring(7);
		logger.info(token);		
		try {
			if (Boolean.TRUE.equals(jwtUtils.validateJwtToken(token))) {
				logger.debug("Token matched is Valid");
				logger.info("Token matched is Valid");
				logger.info("END - validate()");
				return new ResponseEntity<>(new TokenValidation(true), HttpStatus.OK);
			} else {
				throw new TokenException(token,"Invalid Token");
			}
		} catch (Exception e) {
			logger.debug("Invalid token - Bad Credentials Exception");
			logger.info("END Exception - validatingAuthorizationToken()");
			
			return new ResponseEntity<>(new TokenValidation(false), HttpStatus.OK);
		}
		
	}
  @GetMapping(path="/check")
  public ResponseEntity<?> checkStatus(){
	  return new ResponseEntity<>(new TokenValidation(true),HttpStatus.OK);
  }
  
 
}
