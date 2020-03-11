package utils;

import java.io.File;
import java.util.Map;

public class EnviormentVariables {
	private Map<String, String> envMap;
	private String applicationName = "OneCardTracker";
	
	public EnviormentVariables() {
		this.envMap = System.getenv();
	}
	
	public boolean doesSysVarExist() {
		String value = envMap.get(applicationName);
		if (value == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isVarDefault() {
		String value = this.getVariable();
		if (value.equals(System.getProperty("user.home") + File.separator + applicationName)) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getVariable() {
		String value = envMap.get(applicationName);
		if (value == null) {
			return System.getProperty("user.home") + File.separator + applicationName;
		} else {
			return value  + File.separator + applicationName;
		}
	}
	
	public File getStorageLocation() {
		File file = new File(this.getVariable());
		file.mkdirs();
		return file;
	}
}