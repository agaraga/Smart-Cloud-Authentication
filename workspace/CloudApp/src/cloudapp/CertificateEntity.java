package cloudapp;

import javax.persistence.Id;

import com.googlecode.objectify.*;

public class CertificateEntity {
    public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public byte[] getCertificate() {
		return certificate;
	}
	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}
	private @Id String alias;
    byte[] certificate;
    private CertificateEntity() {}
    public CertificateEntity(String alias, byte[] certificate) {
    	this.alias = alias;
    	this.certificate = certificate;
    }
}
