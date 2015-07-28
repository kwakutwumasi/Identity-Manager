package com.quakearts.identity.hibernate;

// Generated 09-Oct-2011 07:47:03 by Hibernate Tools 3.3.0.GA

/**
 * UserRole generated by hbm2java
 */
public class UserRole implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5644371114816368393L;
	private long rid;
	private UserLog userLog;
	private String roleName;
	private boolean valid;

	public UserRole() {
	}

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