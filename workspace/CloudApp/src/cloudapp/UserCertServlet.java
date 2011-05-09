package cloudapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.servlet.http.*;

import org.apache.tools.ant.filters.StringInputStream;

import cloudapp.StringUtils;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class UserCertServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		try {
			resp.setContentType("text/plain");
			String userName = req.getParameter("user");
			if (userName == null || userName.isEmpty()) {
				throw new Exception("user not specified");
			}
			
	        Objectify ofy = ObjectifyService.begin();
        	UserEntity userEntity = ofy.find(UserEntity.class, userName);
        	if (userEntity == null) {
        		throw new Exception("User not found in the database.");
        	}
			String strCertificate = req.getParameter("cert");
			//java.lang.System.out.print("Received: " + strCertificate + "\n");
		
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			StringInputStream is = new StringInputStream(strCertificate);
			X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
			
			cert.checkValidity();
			String issuerLongName = cert.getIssuerX500Principal().getName();
			String subjectLongName = cert.getSubjectX500Principal().getName();
			String issuerCN = StringUtils.getValueByKey(issuerLongName, "CN");
			String subjectCN = StringUtils.getValueByKey(subjectLongName, "CN");
			resp.getWriter().println("Issuer long " + issuerLongName);
			resp.getWriter().println("Issuer CN " + issuerCN);
			resp.getWriter().println("Subject long" + subjectLongName);
			resp.getWriter().println("Subject CN " + subjectCN);
			
			if (issuerCN.isEmpty()) {
				throw new CertificateException("Issuer name not set in certificate");	
			}
			if (subjectCN.isEmpty()) {
				throw new CertificateException("Subject name not set in certificate");	
			}
			if (!userName.equals(subjectCN)) {
				throw new CertificateException("Subject " + subjectCN + " is different from the user name specified");
			}
			// Find the issuer certificate
			CertificateEntity issuerCertEntity = ofy.find(CertificateEntity.class, issuerCN);
			if (issuerCertEntity == null) {
				throw new CertificateException("Issuer certificate not found in the datastore");	
			}
			byte[] issuerCertEncoded = issuerCertEntity.getCertificate();
			ByteArrayInputStream issuerIS = new ByteArrayInputStream(issuerCertEncoded);
			X509Certificate issuerCert = (X509Certificate) cf.generateCertificate(issuerIS);
			PublicKey issuerPublicKey = issuerCert.getPublicKey();
			
			// Verify the signature
			cert.verify(issuerPublicKey);
			
			// Finally store the certificate to the data store		
			CertificateEntity certEntity = new CertificateEntity(userName, cert.getEncoded());	
			ofy.put(certEntity);
			resp.getWriter().println("Certificate successfully added");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.getWriter().println(e.getLocalizedMessage());
		} 
	}
}
