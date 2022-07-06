package com.cts.claimauth.payload.request;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
	
	@NotBlank
	private String userid;
	
	@NotBlank
	private String password;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LoginRequest() {
		
	}
	

}
