package com.quakearts.identity.hibernate;

import java.util.ArrayList;

// Generated 09-Oct-2011 07:47:03 by Hibernate Tools 3.3.0.GA

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class UserLog implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5945992427522415484L;
	@Id
	private long id;
	@Column(nullable=false, length=50)
	private String username;
	@Column(nullable=false, length=200)
	private String password;
	@Column(length=50)
	private String name;
	@Column(nullable=false)
	private boolean valid;
	@OneToMany(mappedBy="userLog")
	private Set<UserRole> userRoleses = new HashSet<UserRole>(0);

	public UserLog() {
	}

	public UserLog(String username, String password, boolean valid) {
		this.username = username;
		this.password = password;
		this.valid = valid;
	}

	public UserLog(String username, String password, String name,
			boolean valid, Set<UserRole> userRoleses) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.valid = valid;
		this.userRoleses = userRoleses;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Set<UserRole> getUserRoles() {
		return this.userRoleses;
	}

	public void setUserRoles(Set<UserRole> userRoleses) {
		this.userRoleses = userRoleses;
	}

	public List<UserRole> getRolesList(){
		return new ArrayList<UserRole>(userRoleses);
	}
}
