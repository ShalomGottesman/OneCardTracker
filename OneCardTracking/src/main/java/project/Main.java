package project;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import utils.IDs;

public class Main {
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("password:");
		//String password = sc.nextLine();
		String password = "";
		sc.close();
		String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
		System.setProperty("webdriver.chrome.driver", path + "chromedriver.exe");
		
		//WebDriver driver = new HtmlUnitDriver();
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.get(IDs.logInURL);
		driver.findElement(By.id(IDs.logIn_usernameID)).sendKeys("sgottes1");
		driver.findElement(By.id(IDs.logIn_passwordID)).sendKeys(password);
		driver.findElement(By.xpath(IDs.logIn_SubmitButton)).click();
		
		driver.get(IDs.balancesURL);
		String balance = driver.findElement(By.xpath(IDs.balancesTotal_XPath)).getText();
		System.out.println(balance);
		
		driver.get(IDs.transactionURL);
		
		WebElement startDate = driver.findElement(By.id(IDs.transactionStartID));
		System.out.println("["+startDate.getText()+"]");
		startDate.clear();
		startDate.sendKeys("01/19/2020");
		System.out.println("["+startDate.getText()+"]");
		
		WebElement endDate = driver.findElement(By.id(IDs.transactionEndID));
		System.out.println(endDate.getText());
		endDate.clear();
		endDate.sendKeys("03/03/2020");
		
		System.out.println(driver.findElement(By.id(IDs.transactionSearchButtomID)).getAttribute("class"));
		driver.findElement(By.id(IDs.transactionSearchButtomID)).click();
		System.out.println(driver.getTitle());
		
		int x = 1;
		ArrayList<Transaction> fullSummery = new ArrayList<Transaction>();
		try {
			while(true) {
				String dateOrig = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[2]/div/div/div[2]/table/tbody/tr["+x+"]/td[1]")).getText();
				String amount = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[2]/div/div/div[2]/table/tbody/tr["+x+"]/td[2]")).getText();
				double price = Double.parseDouble(amount.replace("$", ""));
				DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm:ss a");
				LocalDateTime date = LocalDateTime.parse(dateOrig, inputFormat);
				Transaction trans = new Transaction(date, price);
				fullSummery.add(trans);
				DateTimeFormatter format = DateTimeFormatter.ISO_DATE_TIME;
				System.out.print(date.format(format));
				System.out.println("|"+ "amount: "+amount);
				x++;
			}
		} catch(NoSuchElementException e) {
			driver.quit();
		}
		System.out.println();
		HashMap<LocalDate, DaySummery> dateset = new HashMap<LocalDate, DaySummery>();
		
		for (Transaction trans : fullSummery) {
			LocalDateTime ldt = trans.timeStamp;
			LocalDate localDate = ldt.toLocalDate();
			if (dateset.get(localDate) != null){
				DaySummery day = dateset.get(localDate);
				day.transactions.add(trans);
			} else {
				DaySummery day = new DaySummery(localDate);
				day.transactions.add(trans);
				dateset.put(localDate, day);
			}
		}
		
		Collection<DaySummery> allDays = dateset.values();
		DaySummery[] summeryAry = allDays.toArray(new DaySummery[0]);
		Arrays.sort(summeryAry);
		for(DaySummery ds : allDays) {
			System.out.println(ds.date.toString() + "|" + ds.summery());
		}
		
		
		
	}

}
