package cloudapp;

import java.io.IOException;

import javax.servlet.http.*;

import org.apache.tools.ant.filters.StringInputStream;

import java.security.*;
import java.security.cert.*;


import cloudapp.CertificateEntity;
import cloudapp.StringUtils;

import com.googlecode.objectify.*;

@SuppressWarnings("serial")
public class RootCertServlet extends HttpServlet {
	private void addRootCertificate(String strCertificate) 
		throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		
		// add root certificate
		CertificateFactory cf = CertificateFactory.getInstance("X509");
		StringInputStream is = new StringInputStream(strCertificate);
		X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
		
		cert.checkValidity();
		String issuerLongName = cert.getIssuerX500Principal().getName();
		String subjectLongName = cert.getSubjectX500Principal().getName();
		String issuerCN = StringUtils.getValueByKey(issuerLongName, "CN");
		String subjectCN = StringUtils.getValueByKey(subjectLongName, "CN");
		
		if (issuerCN.isEmpty()) {
			throw new CertificateException("Issuer name not set in certificate");	
		}
		if (subjectCN.isEmpty()) {
			throw new CertificateException("Subject name not set in certificate");	
		}
		if (!issuerCN.equals(subjectCN)) {
			throw new CertificateException("Subject name differs from issuer name for root certificate");
		}
		cert.verify(cert.getPublicKey());
		CertificateEntity certEntity = 
			new CertificateEntity(issuerCN, cert.getEncoded());
		
		Objectify ofy = ObjectifyService.begin();
		ofy.put(certEntity);	
	}
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    	throws IOException {

    	try {
	    	resp.setContentType("text/plain");
	        
        	String content = req.getParameter("content");
        	try {
        		addRootCertificate(content);
        	} catch(CertificateException e) {
        		resp.getWriter().println(e.getMessage());
        		return;
        	}
        	resp.getWriter().println("Certificate successfully added");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
}
