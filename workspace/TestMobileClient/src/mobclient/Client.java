package mobclient;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.net.URL;
import java.net.HttpURLConnection;
import java.lang.System;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.ssl.Base64;

public class Client {	
	private static String signMessage(String msg) 
		throws Exception {

		Key key = getKey();
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initSign((PrivateKey) key);
		byte[] data = msg.getBytes("UTF8");
	    sig.update(data);
		byte[] signatureBytes = sig.sign();
		String result = Base64.encodeBase64URLSafeString(signatureBytes);
		System.out.println("Singature:" + result);
		return result;
	}
	
	private static String encryptMessage(String msg)
		throws Exception {
		X509Certificate cert = getCertificate();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, cert);

		byte[] data = msg.getBytes("UTF8");
		byte[] encryptedBytes = cipher.doFinal(data);
		String result = Base64.encodeBase64URLSafeString(encryptedBytes);
		System.out.println("Cipher:" + result);
		
		return result;
	}
	
	private static void prepareAuthRequest() 
		throws Exception {
		
		String userParam = "algaraga";
		long timeInMillis = System.currentTimeMillis();
		String timestampParam = Long.toString(timeInMillis);
		String password = RandomPassword.generate(8);
		String mac = "00-00-00-00-00-00-00-E0";
		String destParam = "agaraga-test";
		String secretParam = encryptMessage(password + mac);
		
		QueryString qs = new QueryString("user", userParam);
		qs.add("timestamp", timestampParam);
		qs.add("dest", destParam);
		qs.add("secret", secretParam);
		qs.add("signature", signMessage(userParam + timestampParam + destParam + secretParam));
		String query = qs.getQuery();
		System.out.println("Query:" + query);
	}
	
	private static void sendCertificate() 
		throws IOException{
		HttpURLConnection conn = null;
		String urlStr = "http://localhost:8888/usercert?user=algaraga";
		String type = "application/x-www-form-urlencoded";
		
		try {
			FileInputStream fis = new FileInputStream("C:\\Temp\\Cert\\ca1\\server.crt");	
			int fileLength = fis.available();
			System.out.print("Content-Length: " + Integer.toString(fileLength));
			
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", type);
			conn.setRequestProperty("Content-Length", Integer.toString(fileLength));
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();
			OutputStream os = conn.getOutputStream();
						
			byte[] buf = new byte[fileLength];
			fis.read(buf);
			os.write(buf);

			int rc = conn.getResponseCode();
			InputStream is = conn.getInputStream();
			byte[] buf1 = new byte[is.available()];
			is.read(buf1);
			String s = new String(buf1);
			System.out.print("Received:\n" + s);
		}
		finally {
			conn.disconnect();
		}
	}

	private static void verifyCertificate() {	
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			FileInputStream issuerIS = new FileInputStream("C:\\Temp\\Cert\\ca1\\ca.crt");
			X509Certificate issuerCert = (X509Certificate) cf.generateCertificate(issuerIS);
			PublicKey issuerPublicKey = issuerCert.getPublicKey();
			FileInputStream userIS = new FileInputStream("C:\\Temp\\Cert\\ca1\\sarila3.crt");
			X509Certificate userCert = (X509Certificate) cf.generateCertificate(userIS);
			userCert.verify(issuerPublicKey);
			System.out.println("OK");
		} catch (Exception e) {
			
		}		
	}
	
	private static void authenticate() {
		String pass = RandomPassword.generate(8);
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-384");
			byte[] digest = sha.digest(pass.getBytes("UTF-8"));
		    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			DESedeKeySpec desKeySpec1 = new DESedeKeySpec(digest, 0);		    
		    SecretKey secretKey1 = keyFactory.generateSecret(desKeySpec1);
		    //Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		    Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		    DESedeKeySpec desKeySpec2 = new DESedeKeySpec(digest, 24);
		    SecretKey secretKey2 = keyFactory.generateSecret(desKeySpec2);
		    Mac mac = Mac.getInstance("HmacSHA256");
		    
		    cipher.init(Cipher.ENCRYPT_MODE, secretKey1);
		    byte[] encBuf = cipher.doFinal("hhhhhhaaahhh".getBytes());
		    byte[] ivBuf = cipher.getIV();
		    IvParameterSpec ivParamsSpec = new IvParameterSpec(ivBuf); 

		    cipher.init(Cipher.DECRYPT_MODE, secretKey1, ivParamsSpec);
		    byte[] decBuf = cipher.doFinal(encBuf);
		    System.out.println(new String(decBuf));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static Key getKey() 
	throws Exception {
		char[] pass = "morkovka".toCharArray();
		String name = "res/smart.jks";
		String alias = "algaraga";
	    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
	    
	    FileInputStream fis = null;
	    try {
	        fis = new FileInputStream(name);
	        ks.load(fis, pass);
	        Key key = ks.getKey(alias, pass);
	        return key;
	    } finally {
	        if (fis != null) {
	            fis.close();
	        }
	    }		
	}
	
	public static X509Certificate getCertificate()
		throws Exception {
		char[] pass = "morkovka".toCharArray();
		String name = "res/smart.jks";
		String alias = "agaraga-test.appspot.com";
	    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
	    
	    FileInputStream fis = null;
	    try {
	        fis = new FileInputStream(name);
	        ks.load(fis, pass);
	        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
	        return cert;
	    } finally {
	        if (fis != null) {
	            fis.close();
	        }
	    }		
	}
	
	public static void main(String[] args) {		
		try {
			prepareAuthRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
