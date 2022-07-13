package com.cts.claimauth.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import com.cts.claimauth.exception.TokenException;
import com.cts.claimauth.exception.UserNotFoundException;
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
import com.cts.claimauth.service.AuthService;
import com.cts.claimauth.service.AuthServiceImpl;

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
  
//  @Autowired
//  AuthServiceImpl authService;
  @Autowired
  AuthService authService;
  
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
	  JwtResponse jwtResponse=authService.signInResponse(loginRequest.getUserid().toString(), loginRequest.getPassword());
	  return ResponseEntity.ok(jwtResponse);
  }
  
  
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

	  if(authService.existPhoneNo(signUpRequest.getPhoneNo())) {
		  return ResponseEntity.badRequest().body(new MessageResponse("Error: PhoneNo is already taken!"));
	  }
	  if (authService.existEmail(signUpRequest.getEmail())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
          
      }
	    

	  JwtResponse jwtResponse=authService.signupResponse(signUpRequest.getName(), signUpRequest.getEmail(),signUpRequest.getPassword(), signUpRequest.getPhoneNo(),signUpRequest.getAddress());
	  return ResponseEntity.ok(jwtResponse);
  }
  
  @PostMapping("/adminsignup")
  public ResponseEntity<?> registerAdminUser(@Valid @RequestBody SignupRequest signUpRequest) {
	  

	  if(authService.existPhoneNo(signUpRequest.getPhoneNo())) {
		  return ResponseEntity.badRequest().body(new MessageResponse("Error: PhoneNo is already taken!"));
	  }
	  if (authService.existEmail(signUpRequest.getEmail())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
          
      }
	    
	  JwtResponse jwtResponse=authService.adminSignUp(signUpRequest.getName(), signUpRequest.getEmail(),signUpRequest.getPassword(), signUpRequest.getPhoneNo(),signUpRequest.getAddress());
	  return ResponseEntity.ok(jwtResponse);
  }
  
  @PostMapping("/{id}")
  public ResponseEntity<?>  getUser(@PathVariable String id, @RequestBody SignupRequest signUpRequest){
	  try {
	  User searchUser = authService.searchUser(id, signUpRequest);
	  return new ResponseEntity<User>(searchUser,HttpStatus.CREATED);
	  }catch(NoSuchElementException e) {
		  return new ResponseEntity<>(new MessageResponse("Not found the user with id "+id),HttpStatus.BAD_REQUEST);
	  }
		  
	  
  }
  
  @GetMapping(path = "/validate-v1")
   public ResponseEntity<?> validatingAuthorizationToken( @RequestHeader(name = "Authorization") String tokenDup) {
		

		try {
			TokenValidation tokenValidation =authService.validationToken(tokenDup);
			return new ResponseEntity<>(tokenValidation,HttpStatus.ACCEPTED);
		}catch(Exception e) {
			return new ResponseEntity<>(new TokenValidation(false),HttpStatus.UNAUTHORIZED);
		}
	}
  @GetMapping(path = "/validate/{tokenDup}")
  public ResponseEntity<?> validatingAuthorizationTokenService( @PathVariable String tokenDup) {
		String token = tokenDup.substring(7);
	
		try {
			if (Boolean.TRUE.equals(jwtUtils.validateJwtToken(token))) {
				return new ResponseEntity<>(new TokenValidation(true), HttpStatus.ACCEPTED);
			} else {
				throw new TokenException(token,"Invalid Token");
			}
		} catch (Exception e) {
			return new ResponseEntity<>(new TokenValidation(false), HttpStatus.UNAUTHORIZED);
		}
		
	}
  @GetMapping(path="/{id}")
  public ResponseEntity<?> checkUser(@RequestHeader(name = "Authorization") String tokenDup,@PathVariable String id){
	  TokenValidation validationToken = authService.validationToken(tokenDup);
	  if(!validationToken.getToken()) {
		  return new ResponseEntity<>(new MessageResponse("not Validated token"),HttpStatus.UNAUTHORIZED);
	  }
	  try {
//	  Optional<User> findByUserId = userRepository.findByUserId(Long.parseLong(id));
//	  User user=findByUserId.get();
	  User user=authService.userCheck(id);
	  return new ResponseEntity<>(user,HttpStatus.OK);
	  }catch(Exception e) {
		  return new ResponseEntity<>(new MessageResponse("Not found the user with id "+id),HttpStatus.BAD_REQUEST);
	  }
  }
  
  @GetMapping(path="all-user")
  public ResponseEntity<?>getAllUser(@RequestHeader(name = "Authorization") String tokenDup){
	  TokenValidation validationToken=authService.validationToken(tokenDup);
	  if(!validationToken.getToken()) {
		  return new ResponseEntity<>(new MessageResponse("not Validated token"),HttpStatus.UNAUTHORIZED);
	  }
	     List<User> allUser= userRepository.findAll();
	     return new ResponseEntity<>(allUser,HttpStatus.OK);
	  
  }
  @GetMapping(path="user-id/{id}")
  public ResponseEntity<?>shouldUserPresent(@PathVariable String id){
	  if(userRepository.existsByUserId(Long.parseLong(id))) {
		  return new ResponseEntity<>(true,HttpStatus.OK);
	  }else {
		  return new ResponseEntity<>(false,HttpStatus.OK);
	  }
  }
  @GetMapping(path="check-email/{email}")
  public ResponseEntity<?>checkEmail(@PathVariable String email){
	  if (authService.existEmail(email)) {
          return new ResponseEntity<>(true,HttpStatus.OK);   
      }else {
    	  return new ResponseEntity<>(false,HttpStatus.OK);
      }
  }
  
  @GetMapping(path="check-phone/{phoneNo}")
  public ResponseEntity<?>checkPhoneNo(@PathVariable String phoneNo){
	  if(authService.existPhoneNo(phoneNo)) {
		  return new ResponseEntity<>(true,HttpStatus.OK);
	  }else {
		  return new ResponseEntity<>(false,HttpStatus.OK);
	  }
  }
  
 
}
