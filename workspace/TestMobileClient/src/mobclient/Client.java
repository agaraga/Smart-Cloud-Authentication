package mobclient;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.lang.System;
import java.security.SecureRandom;
import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Client {	
	private static String signMessage(String msg) 
		throws Exception {
		
		FileInputStream in = new FileInputStream("res/rsaprivkey.pem");
		long length = in.available();	
		if (length > Integer.MAX_VALUE) {
			return null;
			//throw new IOException("The file is too big");
		}
		byte[] privKeyBytes = new byte[(int)length];	
		in.read(privKeyBytes);
		PKCS8EncodedKeySpec pkcs8Spec = new PKCS8EncodedKeySpec(privKeyBytes);
		KeyFactory          keyFact = KeyFactory.getInstance("RSA");
		PrivateKey          privKey = keyFact.generatePrivate(pkcs8Spec);
		
		return null;
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
	
	public static void main(String[] args) {		
		try {
			authenticate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
