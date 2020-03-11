package project;

import java.io.File;

import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class sandbox {
	
	public static void main(String[] args) {
		String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "windows" + File.separator + "chrome" + File.separator;
		String[] chromeFiles = {"chromedriver79.exe", 
				"chromedriver80.exe", 
				"chromedriver81.exe"
		};
		WebDriver driver = null;
		int x = -1;
		for (x = chromeFiles.length-1; x >= 0; x--) {
			System.out.println(x);
			try {
				driver = setPropertyAndGetChromeDriver(path, chromeFiles, x, "webdriver.chrome.driver");
				break;
			} catch (SessionNotCreatedException e) {
				continue;
			}
		}
		driver.get("https://www.google.com/");
		driver.quit();
		System.out.println(x);
	}
	
	private static WebDriver setPropertyAndGetChromeDriver(String path, String[] files, int fileNum, String prpertyName) {
		System.setProperty(prpertyName, path + files[fileNum]);
		WebDriver driver = new ChromeDriver();
		return driver;
	}
}
