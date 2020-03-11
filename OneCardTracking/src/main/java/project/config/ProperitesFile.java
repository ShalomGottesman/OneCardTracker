package project.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import project.security.Encryption;
import project.security.Encryption.LockAndKey;

public class ProperitesFile {
	Properties propUserIn = new Properties();
	File configFile;
	
	public ProperitesFile(File fileIn) throws FileNotFoundException, IOException{
		configFile = fileIn;
		propUserIn = new Properties();
		propUserIn.load(new FileInputStream(fileIn));
	}
	
	public void setDefaultValues() throws IOException {
		propUserIn.setProperty("user", "");
		propUserIn.setProperty("password", "");
		propUserIn.setProperty("key", "");
		saveProps();
	}
	
	public void saveProps() throws IOException{
		OutputStream userInputOut = new FileOutputStream(configFile.getAbsolutePath());
		propUserIn.store(userInputOut, "User Input");
	}
	
	public void encryptAndStorePassword(String password) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
		Encryption enc = new Encryption("zFLVIU4RWrTigwmL");
		LockAndKey lak = enc.encrypt(password);
		storePasswordAndkey(lak.encyptedString, lak.key);
	}
	
	public void storePasswordAndkey(String encryptedPasswrod, String key) throws IOException {
		propUserIn.setProperty("password", encryptedPasswrod);
		propUserIn.setProperty("key", key);
		saveProps();
	}
	
	public String getUsername() {
		return propUserIn.getProperty("user");
	}
	
	public String getEncryptedPassword() {
		return propUserIn.getProperty("password");
	}
	
	public String getKey() {
		return propUserIn.getProperty("key");
	}
	
	public String decryptPassword() throws InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		Encryption enc = new Encryption("zFLVIU4RWrTigwmL");
		return enc.decrypt(getEncryptedPassword(), getKey());
	}
	
	public void setUsername(String user) throws IOException {
		propUserIn.setProperty("user", user);
		saveProps();
	}
}
