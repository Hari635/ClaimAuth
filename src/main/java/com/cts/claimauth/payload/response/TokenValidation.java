package com.cts.claimauth.payload.response;

public class TokenValidation {
    private boolean tokenStatus;

	public boolean getToken() {
		return tokenStatus;
	}

	public void setToken(boolean token) {
		this.tokenStatus = token;
	}

	public TokenValidation(boolean token) {
		super();
		this.tokenStatus = token;
	}

	public TokenValidation() {
		super();
	}
    
   
}
