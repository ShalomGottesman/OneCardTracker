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

import org.joda.time.LocalDate;

import project.ExceptionDays;
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
	
	public int getAmountOfExceptionDays() {
		if (propUserIn.getProperty("exceptionDaysCount") == null) {
			return 0;
		} else {
			return Integer.parseInt(propUserIn.getProperty("exceptionDaysCount"));
		}
	}
	
	public void addExceptionDays(ExceptionDays exc) throws IOException {
		if (propUserIn.getProperty("exceptionDaysCount") == null) {
			propUserIn.setProperty("exceptionDaysCount", "0");
			saveProps();
		} 
		int currentCount = Integer.parseInt(propUserIn.getProperty("exceptionDaysCount"));
		String value = exc.startDate.getYear()+"-"+exc.startDate.getMonthOfYear()+"-"+exc.startDate.getDayOfMonth()+"/"+
						exc.endDate.getYear()+"-"+exc.endDate.getMonthOfYear()+"-"+exc.endDate.getDayOfMonth();
		propUserIn.setProperty("execptionDay" + (currentCount+1), value);
		propUserIn.setProperty("exceptionDaysCount", ""+(currentCount+1));
		saveProps();
	}
	
	public void removeExceptionDays(int index) throws IOException {
		String currentCount = propUserIn.getProperty("exceptionDaysCount");
		if (currentCount == null || currentCount.equals("")) {
			throw new IllegalStateException("There currently no Exception Day Ranges on file to remove from");
		}
		int current = Integer.parseInt(currentCount);
		if (current < index || index <= 0 ) {
			throw new IllegalArgumentException("index of Exception Days to be removed not in range, or less or equal to than zero");
		}
		if (current == index) {
			propUserIn.remove("execptionDay"+index);
			propUserIn.setProperty("exceptionDaysCount", ""+(current-1));
		} else {
			String currentMax = propUserIn.getProperty("execptionDay"+currentCount);
			propUserIn.remove("execptionDay"+currentCount);
			propUserIn.setProperty("execptionDay"+index, currentMax);
		}
		saveProps();
	}
	
	public ExceptionDays getExceptionDays(int setNumber) {
		int currentCount = Integer.parseInt(propUserIn.getProperty("exceptionDaysCount"));
		if(setNumber > currentCount || setNumber < 0) {
			throw new IllegalArgumentException("no such set exists");
		}
		String value = propUserIn.getProperty("execptionDay" + setNumber);
		String[] dates = value.split("/");
		LocalDate start = LocalDate.parse(dates[0]);
		LocalDate end = LocalDate.parse(dates[1]);
		return new ExceptionDays(start, end);
	}
	
	public ExceptionDays[] getAllExceptionDays() {
		if (propUserIn.getProperty("exceptionDaysCount") == null || propUserIn.getProperty("exceptionDaysCount").equals("")) {
			propUserIn.setProperty("exceptionDaysCount", "0");
		}
		int currentCount = Integer.parseInt(propUserIn.getProperty("exceptionDaysCount"));
		ExceptionDays[] allExceptionDays = new ExceptionDays[currentCount];
		for (int x = 1; x <= currentCount; x++) {
			allExceptionDays[x-1] = getExceptionDays(x);
		}
		return allExceptionDays;
	}
	
	public void setStartDate(LocalDate date) throws IOException {
		propUserIn.setProperty("termStart", date.getYear()+"-"+date.getMonthOfYear()+"-"+date.getDayOfMonth());
		saveProps();
	}
	
	public LocalDate getStartDate() {
		String strDate = propUserIn.getProperty("termStart");
		if ( strDate == null || strDate.equals("")) {
			return null;
		}
		else return LocalDate.parse(strDate);
	}
	
	public void setEndDate(LocalDate date) throws IOException {
		propUserIn.setProperty("termEnd", date.getYear()+"-"+date.getMonthOfYear()+"-"+date.getDayOfMonth());
		saveProps();
	}
	
	public LocalDate getEndDate() {
		String strDate = propUserIn.getProperty("termEnd");
		if ( strDate == null || strDate.equals("")) {
			return null;
		}
		else return LocalDate.parse(strDate);
	}
}
