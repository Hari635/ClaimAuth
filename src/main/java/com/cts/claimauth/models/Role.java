package com.cts.claimauth.models;
import javax.persistence.*;

@Entity
@Table(name="roles")
public class Role {
   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;
   
   @Enumerated(EnumType.STRING)
   @Column(length=20)
   private URole name;

public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}

public URole getName() {
	return name;
}

public void setName(URole name) {
	this.name = name;
}
public Role() {
	
}

public Role(URole name) {
	this.name = name;
}
   
   
   
}
