package com.cts.claimauth.payload.response;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type="Bearer";
    private Long userid;
    private String name;
    private String email;
    private List<String> roles;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public JwtResponse(String token, Long userid, String name, String email, List<String> roles) {
		super();
		this.token = token;
		this.userid = userid;
		this.name = name;
		this.email = email;
		this.roles = roles;
	}
	
}
