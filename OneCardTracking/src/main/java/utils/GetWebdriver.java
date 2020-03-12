package utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class GetWebdriver {
	
	public static WebDriver getWebDriver() {
		String driverName = getDriverName();
		File driverFile = new File(new EnviormentVariables().getVariable()+ File.separator + driverName);
		System.setProperty("phantomjs.binary.path", driverFile.getAbsolutePath());
		WebDriver driver = new PhantomJSDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		return driver;
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

}
