package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import project.config.ProperitesFile;
import utils.EnviormentVariables;
import utils.OS;

public class FilesCheck {
	public static void main(String[] args) throws IOException {
		//first check the config file
		EnviormentVariables env = new EnviormentVariables();
		File storageLocation = new File(env.getVariable()+ File.separator);
		storageLocation.mkdirs();
		File configurationFile = new File(env.getVariable()+ File.separator + "configuration.properties");
		ProperitesFile props = null;
		if (!configurationFile.exists()) {
			configurationFile.createNewFile();
			props = new ProperitesFile(configurationFile);
			props.setDefaultValues();
		} else {
			props = new ProperitesFile(configurationFile);
		}
		if(props.getUsername() == null) {
			props.setUsername("");
		}
		if(props.getEncryptedPassword()==null || props.getKey()==null) {
			String user = props.getUsername();
			props.setDefaultValues();
			props.setUsername(user);
		}
		
		//next check the phantomjs driver
		String driverName = getDriverName();
		File driverFile = new File(env.getVariable()+ File.separator + driverName);
		if(!driverFile.exists()) { 
			URL classResource = FilesCheck.class.getResource("PreCheck.class");
			if (classResource != null && classResource.toString().contains("jar")) { //extract driver from jar file
				String inPath = "/resources/" + getResourceSubFolder() + "/" + getDriverName();
				InputStream in = FilesCheck.class.getResourceAsStream(inPath);
				Files.copy(in, driverFile.toPath());
			} else {//we are running from .class files, copy file directly
				File driver = getDriverLocation();
				Files.copy(driver.toPath(), new FileOutputStream(driverFile));
			}
		} 
	}
	
	private static String getDriverName() {
		String driverName = "";
		if(OS.isMac() || OS.isUnix()) {
			driverName = "phantomjs";
		}
		if(OS.isWindows()) {
			driverName = "phantomjs.exe";
		}
		if(driverName.equals("")){
			throw new IllegalStateException("Operating System not supported");
		}
		return driverName;
	}
	
	private static File getDriverLocation() {
		String path = System.getProperty("user.dir") + File.separator + "src"  + File.separator + "main" + File.separator + "resources"+ File.separator;
		path += getResourceSubFolder() + File.separator + getDriverName();
		return new File(path);
	}
	
	private static String getResourceSubFolder() {
		String path = "";
		if(OS.isUnix()) {
			path = "linux"; 
		}
		if(OS.isMac()) {
			path = "mac";
		}
		if(OS.isWindows()) {
			path = "windows";
		}
		return path;
	}
}
