package project.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Encryption {
	IvParameterSpec ivParSpec;
	String alphabet = "1234567890!@$^&*qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
	
	public Encryption(IvParameterSpec iv) {
		this.ivParSpec = iv;
	}
	
	public Encryption(String ivBase) {
		try {
			this.ivParSpec = new IvParameterSpec(ivBase.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public LockAndKey encrypt(String toBeEncrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		String keyString = "";
		Random rand = new Random();
		for (int x = 0; x < 16; x ++) {
			keyString = keyString + alphabet.charAt(rand.nextInt(alphabet.length()));
		}
		SecretKeySpec skeySpec = new SecretKeySpec(keyString.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParSpec);
        byte[] encrypted = cipher.doFinal(toBeEncrypted.getBytes());
        LockAndKey pair = new LockAndKey(Base64.encodeBase64String(encrypted), keyString);
        return pair;
	}
	
	public String decrypt(LockAndKey pair) throws UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		SecretKeySpec skeySpec = new SecretKeySpec(pair.key.getBytes("UTF-8"), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, this.ivParSpec);
        byte[] original = cipher.doFinal(Base64.decodeBase64(pair.encyptedString));
        return new String(original);
	}
	
	public String decrypt(String password, String key) throws InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		LockAndKey lak = new LockAndKey(password, key);
		return decrypt(lak);
	}
	
	public class LockAndKey {
		public String encyptedString;
		public String key;
		
		public LockAndKey(String string, String key){
			this.encyptedString = string;
			this.key = key;
		}
	}
}
