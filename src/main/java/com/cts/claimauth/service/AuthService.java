package com.cts.claimauth.service;

import com.cts.claimauth.models.User;
import com.cts.claimauth.payload.request.SignupRequest;
import com.cts.claimauth.payload.response.JwtResponse;
import com.cts.claimauth.payload.response.TokenValidation;

public interface AuthService {
	 public JwtResponse signInResponse(String userId,String password);
	 public boolean existPhoneNo(String phoneNo);
	 public boolean existEmail(String email);
	 public JwtResponse signupResponse(String name,String email,String password,String phoneNo,String address);
	 public JwtResponse adminSignUp(String name, String email, String password, String phoneNo, String address);
	 public TokenValidation validationToken(String tokenDup);
	 public User searchUser(String id,SignupRequest signUpRequest);
	 public User userCheck(String id);
}
