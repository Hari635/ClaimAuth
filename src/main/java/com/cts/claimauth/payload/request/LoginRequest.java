package com.cts.claimauth.payload.request;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
	
	
	private Long userid;
	
	@NotBlank
	private String password;

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
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
