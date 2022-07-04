package com.cts.claimauth.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="users",uniqueConstraints= {
		@UniqueConstraint(columnNames="email"),
		@UniqueConstraint(columnNames="phoneNo")
})
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;
    
    @NotBlank
    @Size(min=4,max=20)
    private String name;
    
    @NotBlank
    @Size(max=50)
    @Email
    private String email;
    
    @NotBlank
    @Size(min=8,max=20)
    private String password;
    
    @NotBlank
    @Size(min=10,max=10)
    private String phoneNo;
    
    @NotBlank
    private String address;
    
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="user_roles",joinColumns=@JoinColumn(name="user_id"),inverseJoinColumns=@JoinColumn(name="role_id"))
    private String roles;

    public User() {
    	
    }
	

	public User(@NotBlank @Size(min = 4, max = 20) String name, @NotBlank @Size(max = 50) @Email String email,
			@NotBlank @Size(min = 8, max = 20) String password, @NotBlank @Size(min = 10, max = 10) String phoneNo,
			@NotBlank String address) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.phoneNo = phoneNo;
		this.address = address;
	}


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
    
}
