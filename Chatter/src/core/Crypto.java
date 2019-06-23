package core;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

// Blowfish cryptography class
public class Crypto {

	// Static symmetric key
	private static String key = "key12345";
	
	// Encrypts form of String type and returns byte array
	public static byte[] encrypt(String strClearText) {
		byte[] encrypted = null;
		
		try {
			SecretKeySpec skeyspec = new SecretKeySpec(key.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			encrypted = cipher.doFinal(strClearText.getBytes());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}
	
	// Decrypts form of type byte array and returns original form as String
	public static String decrypt(byte[] strEncrypted) {
		String strData = "";
		
		try {
			SecretKeySpec skeyspec = new SecretKeySpec(key.getBytes(),"Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			byte[] decrypted = cipher.doFinal(strEncrypted);
			strData = new String(decrypted);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strData;
	}
}


