package de.mpg.mpdl.doi.security;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="roles")
public class DoxiRole {
	
	@Column(name="role", nullable=false)
	@Id
	private String role;
	
	
	@Id
	@Column(name="username", nullable=false)
	private String username;
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}




}