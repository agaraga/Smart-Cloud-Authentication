package cloudapp;

import javax.persistence.Id;
import com.googlecode.objectify.*;

public class SessionEntity {
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public long getSessionStartTime() {
		return sessionStartTime;
	}
	public void setSessionStartTime(long sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}
	public long getAuthExpirationTime() {
		return authExpirationTime;
	}
	public void setAuthExpirationTime(long authExpirationTime) {
		this.authExpirationTime = authExpirationTime;
	}
	public int getNonce() {
		return nonce;
	}
	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public byte[] getSessionKeyMaterial() {
		return sessionKeyMaterial;
	}
	public void setSessionKeyMaterial(byte[] sessionKeyMaterial) {
		this.sessionKeyMaterial = sessionKeyMaterial;
	}
	public static long getMaxlifetime() {
		return maxLifeTime;
	}
	private @Id String alias;
	private long sessionStartTime;
	private long authExpirationTime;
	private int nonce;
	private String password;
	private byte[] sessionKeyMaterial;
	// maximum life time of a session in milliseconds
	private static final long maxLifeTime = 36000000; 
	public SessionEntity(String alias) {
		this.alias = alias;
	}
}
 