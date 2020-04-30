package com.quakearts.identity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class UserRole implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5644371114816368393L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long rid;
	@ManyToOne
	private UserLog userLog;
	@Column(nullable=false, length=50)
	private String roleName;
	@Column(nullable=false)
	private boolean valid;

	public UserRole() {}

	public UserRole(UserLog userLog, String roleName, boolean valid) {
		this.userLog = userLog;
		this.roleName = roleName;
		this.valid = valid;
	}

	public long getRid() {
		return this.rid;
	}

	public void setRid(long rid) {
		this.rid = rid;
	}

	public UserLog getUserLog() {
		return this.userLog;
	}

	public void setUserLog(UserLog userLog) {
		this.userLog = userLog;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
