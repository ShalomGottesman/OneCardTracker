package project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFScatterChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import utils.IDs;

public class Main {
	static LocalDate timeStart = LocalDate.parse("1900-01-01");
	static LocalDate startingDate = LocalDate.parse("2020-01-19");
	static LocalDate endingDate = LocalDate.parse("2020-05-28");
	static LocalDate startingDateObj = LocalDate.now();
	static Set<ExceptionDays> exceptionSet = new HashSet<ExceptionDays>();
	
	public static void main(String[] args) throws IOException {
		exceptionSet.add(new ExceptionDays(LocalDate.parse("2020-04-07"), LocalDate.parse("2020-04-18")));//pesach
		exceptionSet.add(new ExceptionDays(LocalDate.parse("2020-04-25"), LocalDate.parse("2020-04-29")));//pesach
		
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
		balance = balance.substring(7);
		System.out.println("["+balance+"]");
		double balanceDouble = Double.parseDouble(balance.replace("$", "").replace(",", ""));
		
		driver.get(IDs.transactionURL);
		
		WebElement startDate = driver.findElement(By.id(IDs.transactionStartID));
		System.out.println("["+startDate.getText()+"]");
		startDate.clear();
		String startDateimput = startingDate.getMonthOfYear()+"/"+startingDate.getDayOfMonth()+"/"+startingDate.getYear(); 
		startDate.sendKeys(startDateimput);
		System.out.println("["+startDate.getText()+"]");
		
		WebElement endDate = driver.findElement(By.id(IDs.transactionEndID));
		System.out.println(endDate.getText());
		endDate.clear();
		
		String dateInput = startingDateObj.getMonthOfYear()+"/"+startingDateObj.getDayOfMonth()+"/"+startingDateObj.getYear();
		endDate.sendKeys(dateInput);
		
		System.out.println(driver.findElement(By.id(IDs.transactionSearchButtomID)).getAttribute("class"));
		driver.findElement(By.id(IDs.transactionSearchButtomID)).click();
		System.out.println(driver.getTitle());
		
		double balanceCopy = balanceDouble;
		int x = 1;
		ArrayList<Transaction> fullSummery = new ArrayList<Transaction>();
		try {
			while(true) {
				String dateOrig = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[2]/div/div/div[2]/table/tbody/tr["+x+"]/td[1]")).getText();
				String amount = driver.findElement(By.xpath("/html/body/div[1]/div[2]/section/div[2]/div/div/div[2]/table/tbody/tr["+x+"]/td[2]")).getText();
				double price = Double.parseDouble(amount.replace("$", ""));
				DateTimeFormatter inputFormat = DateTimeFormat.forPattern("MM/dd/yyyy h:mm:ss a");
				LocalDateTime date = LocalDateTime.parse(dateOrig, inputFormat);
				Transaction trans = new Transaction(date, price);
				balanceCopy -= price;
				fullSummery.add(trans);
				//DateTimeFormatter format = DateTimeFormatter.ISO_DATE_TIME;
				//System.out.print(date.format(format));
				//System.out.println("|"+ "amount: "+amount);
				x++;
			}
		} catch(NoSuchElementException e) {
			driver.quit();
		}
		System.out.println("orig value: " + balanceCopy);
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
		System.out.println("current blance: " + balanceDouble);
		for(int y = 0; y < summeryAry.length; y++) {
			System.out.println(summeryAry[y].date.toString() + "|" + summeryAry[y].summery());
			balanceDouble -= summeryAry[y].summery();
		}
		System.out.println("orig balance: " + balanceDouble);
		
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		Row header = getRow(sheet, 0);
		header.createCell(0).setCellValue("Date");
		header.createCell(1).setCellValue("Amount Spent");
		header.createCell(2).setCellValue("Balance");
		header.createCell(4).setCellValue("Base Date");
		header.createCell(5).setCellValue("Base Balance");
		header.createCell(6).setCellValue("Total Days Range");
		header.createCell(8).setCellValue("Exception start");
		header.createCell(9).setCellValue("Exception End");
		header.createCell(10).setCellValue("Range Days");
		header.createCell(12).setCellValue("Total Exception Days");
		
		CellStyle dateStyle = wb.createCellStyle();
		dateStyle.setDataFormat(wb.createDataFormat().getFormat("mm/dd/yy"));
		/*set starting info */
		Row infoStart = getRow(sheet, 1);
		Cell cell = infoStart.createCell(0);
		cell.setCellStyle(dateStyle);
		cell.setCellFormula("DATE("+startingDate.getYear()+", "+startingDate.getMonthOfYear()+", "+startingDate.getDayOfMonth()+")-1");
		Cell priceCellInfo = infoStart.createCell(1);
		Cell balanceCellInfo = infoStart.createCell(2);
		priceCellInfo.setCellValue(0);
		balanceCellInfo.setCellValue(balanceDouble);
		double balanceDoubleCopy = balanceDouble;
		/* write all transactions */
		for(int y = 0; y < summeryAry.length; y++) {
			Row row = getRow(sheet, y+2);
			Cell dateCell = row.createCell(0);
			//dateCell.setCellStyle(dateStyle);
			Cell priceCell = row.createCell(1);
			Cell balanceCell = row.createCell(2);
			LocalDate date = summeryAry[y].date;
			dateCell.setCellStyle(dateStyle);
			dateCell.setCellFormula(dateToDateFormula(date));
			priceCell.setCellValue(summeryAry[y].summery());
			balanceCell.setCellValue(balanceDouble+=summeryAry[y].summery());
		}
		
		/*write origional base without exception days */
		getRow(sheet, 1).createCell(4).setCellFormula(dateToDateFormula(startingDate)+"-1");
		applyDateFormatting(sheet, 1, 4, dateStyle);
		getRow(sheet, 1).createCell(5).setCellValue(balanceDoubleCopy);
		getRow(sheet, 1).createCell(6).setCellFormula("E3-E2");//total day range
		getRow(sheet, 2).createCell(4).setCellFormula(dateToDateFormula(endingDate));
		applyDateFormatting(sheet, 2, 4, dateStyle);
		getRow(sheet, 2).createCell(5).setCellValue(0);
		
		/*write all exception days*/
		exceptionSet = verifyRanges(exceptionSet);
		ExceptionDays[] exceptionDaysRange = exceptionSet.toArray(new ExceptionDays[0]);
		Arrays.sort(exceptionDaysRange);
		for (x = 0; x < exceptionDaysRange.length; x++) {
			Row row = getRow(sheet, x+1);
			ExceptionDays ed = exceptionDaysRange[x];
			row.createCell(8).setCellFormula(dateToDateFormula(ed.startDate));
			applyDateFormatting(sheet, x+1, 8, dateStyle);
			row.createCell(9).setCellFormula(dateToDateFormula(ed.endDate));
			applyDateFormatting(sheet, x+1, 9, dateStyle);
			row.createCell(10).setCellFormula("J"+(x+2)+"-I"+(x+2));
		}
		getRow(sheet, 1).createCell(12).setCellFormula("SUM(K2:K"+ (2+exceptionDaysRange.length)+")");
		
		/*calculate average spending allowance with exception days */
		getRow(sheet, 4).createCell(4).setCellValue("Total Days");
		getRow(sheet, 4).createCell(5).setCellValue("Average Allowance");
		getRow(sheet, 5).createCell(4).setCellFormula("G2-M2");
		getRow(sheet, 5).createCell(5).setCellFormula("F2/E6");
		
		/*recalculate baseline with exception days*/
		getRow(sheet, 7).createCell(4).setCellValue("Augmented Base");
		getRow(sheet, 8).createCell(4).setCellValue("Date");
		getRow(sheet, 8).createCell(5).setCellValue("Balance");
		
		getRow(sheet, 9).createCell(4).setCellFormula(dateToDateFormula(startingDate)+"-1");
		applyDateFormatting(sheet, 9, 4, dateStyle);
		getRow(sheet, 9).createCell(5).setCellValue(balanceDoubleCopy);
		for (x = 0; x < exceptionDaysRange.length; x++) {
			int rowNumForStartOfInfo = 10+(2*x);
			ExceptionDays exc = exceptionDaysRange[x];
			getRow(sheet, rowNumForStartOfInfo).createCell(4).setCellFormula(dateToDateFormula(exc.startDate));
			applyDateFormatting(sheet, rowNumForStartOfInfo, 4, dateStyle);
			getRow(sheet, rowNumForStartOfInfo).createCell(5).setCellFormula("F"+(rowNumForStartOfInfo)+"-(F6*(E"+(rowNumForStartOfInfo+1)+"-E"+(rowNumForStartOfInfo)+"))");
			
			getRow(sheet, rowNumForStartOfInfo+1).createCell(4).setCellFormula(dateToDateFormula(exc.endDate));
			applyDateFormatting(sheet, rowNumForStartOfInfo+1, 4, dateStyle);
			getRow(sheet, rowNumForStartOfInfo+1).createCell(5).setCellFormula("F"+(rowNumForStartOfInfo+1));
		}
		getRow(sheet, 9+(2*exceptionDaysRange.length)+1).createCell(4).setCellFormula(dateToDateFormula(endingDate));
		applyDateFormatting(sheet, 9+(2*exceptionDaysRange.length)+1, 4, dateStyle);
		getRow(sheet, 9+(2*exceptionDaysRange.length)+1).createCell(5).setCellValue(0);
		
		
		
		XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 8, 6+(2*exceptionDaysRange.length), 15, 26+(2*exceptionDaysRange.length));
        XSSFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);
        XDDFValueAxis bottomAxis = chart.createValueAxis(AxisPosition.BOTTOM);
        
        bottomAxis.setMinimum(Days.daysBetween(timeStart, startingDate).getDays());
        bottomAxis.setMaximum(Days.daysBetween(timeStart, endingDate).getDays() + 1);
        System.out.println(Days.daysBetween(timeStart, startingDate).getDays() - 1);
        System.out.println(Days.daysBetween(timeStart, endingDate).getDays() + 1);
        bottomAxis.setTitle("Date"); // https://stackoverflow.com/questions/32010765
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Balance");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        XDDFDataSource<Double> xs1 = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet, new CellRangeAddress(1, 1+summeryAry.length, 0, 0));
        XDDFNumericalDataSource<Double> ys1 = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet, new CellRangeAddress(1, 1+summeryAry.length, 2, 2));
        
        XDDFDataSource<Double> xs2 = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet, new CellRangeAddress(9, 9+(2*exceptionDaysRange.length)+1, 4, 4));
        XDDFNumericalDataSource<Double> ys2 = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet, new CellRangeAddress(9, 9+(2*exceptionDaysRange.length)+1, 5, 5));


        XDDFScatterChartData data = (XDDFScatterChartData) chart.createData(ChartTypes.SCATTER, bottomAxis, leftAxis);
        XDDFScatterChartData.Series series1 = (XDDFScatterChartData.Series) data.addSeries(xs1, ys1);
        series1.setTitle("Current Balance", null); // https://stackoverflow.com/questions/21855842
        series1.setSmooth(true); // https://stackoverflow.com/questions/39636138
        XDDFScatterChartData.Series series2 = (XDDFScatterChartData.Series) data.addSeries(xs2, ys2);
        series2.setTitle("BaseLine", null);
        chart.plot(data);

        solidLineSeries(data, 0, PresetColor.ORANGE);
        solidLineSeries(data, 1, PresetColor.DARK_BLUE);
		
		
		
		File currDir = new File(".");
		String path2 = currDir.getAbsolutePath();
		String fileLocation = path2.substring(0, path2.length() - 1) + "temp.xlsx";
		 
		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		wb.write(outputStream);
		wb.close();
		
		
		
		
		
	}
	 private static void solidLineSeries(XDDFChartData data, int index, PresetColor color) {
	        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(color));
	        XDDFLineProperties line = new XDDFLineProperties();
	        line.setFillProperties(fill);
	        XDDFChartData.Series series = data.getSeries().get(index);
	        XDDFShapeProperties properties = series.getShapeProperties();
	        if (properties == null) {
	            properties = new XDDFShapeProperties();
	        }
	        properties.setLineProperties(line);
	        series.setShapeProperties(properties);
	    }
	
	private static String dateToDateFormula(LocalDate date) {
		return "DATE("+date.getYear()+", "+date.getMonthOfYear()+", "+date.getDayOfMonth()+")";
	}
	
	private static void applyDateFormatting(Sheet sheet, int row, int column, CellStyle dateStyle) {
		getRow(sheet, row).getCell(column).setCellStyle(dateStyle);
	}
	
	private static Row getRow(Sheet sheet, int row) {
		Row temp = sheet.getRow(row);
		if (temp == null) {
			temp = sheet.createRow(row);
		}
		return temp;
	}
	
	private static Set<ExceptionDays> verifyRanges(Set<ExceptionDays> set){
		Set<ExceptionDays> itemsToRemove = new HashSet<ExceptionDays>();
		Set<ExceptionDays> itemsToAdd = new HashSet<ExceptionDays>();
		for (ExceptionDays exc : set) {
			for (ExceptionDays exc2 : set) {
				if (exc.startDate.isBefore(exc2.startDate) && exc2.startDate.isBefore(exc.endDate)) {//combine items and readd
					itemsToRemove.add(exc);
					itemsToRemove.add(exc2);
					itemsToAdd.add(new ExceptionDays(exc.startDate, exc2.endDate));
				}
			}
		}
		for (ExceptionDays removeItem : itemsToRemove) {
			set.remove(removeItem);
		}
		for (ExceptionDays addItem : itemsToAdd) {
			set.add(addItem);
		}
		return set;
	}

}
