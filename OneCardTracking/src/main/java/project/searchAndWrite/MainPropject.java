package project.searchAndWrite;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

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

import project.DaySummery;
import project.ExceptionDays;
import project.Transaction;
import project.config.ProperitesFile;
import utils.EnviormentVariables;
import utils.GetWebdriver;
import utils.IDs;

public class MainPropject {
	static LocalDate timeStart = LocalDate.parse("1900-01-01");
	static LocalDate startingDateObj = LocalDate.now();
	
	public static void main() throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {		
		WebDriver driver = GetWebdriver.getWebDriver();
		try {
			File configurationFile = new File(new EnviormentVariables().getVariable()+ File.separator + "configuration.properties");
			ProperitesFile props = new ProperitesFile(configurationFile);
			login(driver, props);
			double currentDouble = getCurrentBalance(driver);
			DaySummery[] summerAry = getAndReadAllTransactions(driver, props, currentDouble);
			double origionalBalance = getOrigionalBalance(summerAry, currentDouble);
			writeAllToExcel(props, origionalBalance, currentDouble, summerAry);
		} catch (NoSuchElementException e) {
			driver.quit();
			e.printStackTrace();
		}
	}
	
	private static void login(WebDriver driver, ProperitesFile props) throws InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		driver.get(IDs.logInURL);
		driver.findElement(By.id(IDs.logIn_usernameID)).sendKeys(props.getUsername());
		driver.findElement(By.id(IDs.logIn_passwordID)).sendKeys(props.decryptPassword());
		driver.findElement(By.xpath(IDs.logIn_SubmitButton)).click();
	}
	
	private static double getCurrentBalance(WebDriver driver) {
		driver.get(IDs.balancesURL);
		String balance = driver.findElement(By.xpath(IDs.balancesTotal_XPath)).getText();
		balance = balance.substring(7);
		return Double.parseDouble(balance.replace("$", "").replace(",", ""));
	}
	
	private static DaySummery[] getAndReadAllTransactions(WebDriver driver, ProperitesFile props, double currentBalance) {
		driver.get(IDs.transactionURL);
		LocalDate startingDate = props.getStartDate();
		
		WebElement startDateInputField = driver.findElement(By.id(IDs.transactionStartID));
		String startDateimput = startingDate.getMonthOfYear()+"/"+startingDate.getDayOfMonth()+"/"+startingDate.getYear(); 
		startDateInputField.clear();
		startDateInputField.sendKeys(startDateimput);
		
		WebElement endDateInputField = driver.findElement(By.id(IDs.transactionEndID));
		endDateInputField.clear();
		String dateInput = startingDateObj.getMonthOfYear()+"/"+startingDateObj.getDayOfMonth()+"/"+startingDateObj.getYear();
		endDateInputField.sendKeys(dateInput);
		
		driver.findElement(By.id(IDs.transactionSearchButtomID)).click();
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
				fullSummery.add(trans);
				x++;
			}
		} catch(NoSuchElementException e) {
			driver.quit();
		}
		
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
		return summeryAry;
	}
	
	private static double getOrigionalBalance(DaySummery[] summeryAry, double currentBalance) {
		for(int y = 0; y < summeryAry.length; y++) {
			currentBalance -= summeryAry[y].summery();
		}
		return currentBalance;
	}
	
	private static void writeAllToExcel(ProperitesFile props, final double origionalBalance, final double currentBalance, DaySummery[] summeryAry) throws IOException {
		LocalDate startingDate = props.getStartDate();
		LocalDate endingDate = props.getEndDate();
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
		cell.setCellFormula(dateToDateFormula(startingDate)+"-1");
		Cell priceCellInfo = infoStart.createCell(1);
		Cell balanceCellInfo = infoStart.createCell(2);
		priceCellInfo.setCellValue(0);
		balanceCellInfo.setCellValue(origionalBalance);
		double origionalBalanceCopy = origionalBalance;
		/* write all transactions */
		int y = 0;
		for(y = 0; y < summeryAry.length; y++) {
			Row row = getRow(sheet, y+2);
			Cell dateCell = row.createCell(0);
			//dateCell.setCellStyle(dateStyle);
			Cell priceCell = row.createCell(1);
			Cell balanceCell = row.createCell(2);
			LocalDate date = summeryAry[y].date;
			dateCell.setCellStyle(dateStyle);
			dateCell.setCellFormula(dateToDateFormula(date));
			priceCell.setCellValue(summeryAry[y].summery());
			balanceCell.setCellValue(origionalBalanceCopy+=summeryAry[y].summery());
		}
		/*write current date and balance */
		getCell(getRow(sheet, y+2),0).setCellStyle(dateStyle);
		getCell(getRow(sheet, y+2),0).setCellFormula(dateToDateFormula(LocalDate.now()));//set to today
		getCell(getRow(sheet, y+2),1).setCellValue(0);
		getCell(getRow(sheet, y+2),2).setCellValue(currentBalance);
		
		/*write origional base without exception days */
		getCell(getRow(sheet, 1),4).setCellFormula(dateToDateFormula(startingDate)+"-1");
		applyDateFormatting(sheet, 1, 4, dateStyle);
		getCell(getRow(sheet, 1),5).setCellValue(origionalBalance);
		getCell(getRow(sheet, 1),6).setCellFormula("E3-E2");//total day range
		getCell(getRow(sheet, 2),4).setCellFormula(dateToDateFormula(endingDate));
		applyDateFormatting(sheet, 2, 4, dateStyle);
		getCell(getRow(sheet, 2),5).setCellValue(0);
		
		ExceptionDays[] exceptionDayAry = props.getAllExceptionDays();
		Set<ExceptionDays> exceptionDaysSet = new HashSet<ExceptionDays>();
		for(int x = 0; x < exceptionDayAry.length; x++) {
			exceptionDaysSet.add(exceptionDayAry[x]);
		}
		/*write all exception days*/
		exceptionDaysSet = verifyRanges(exceptionDaysSet);
		ExceptionDays[] exceptionDaysRange = exceptionDaysSet.toArray(new ExceptionDays[0]);
		Arrays.sort(exceptionDaysRange);
		for (int x = 0; x < exceptionDaysRange.length; x++) {
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
		getCell(getRow(sheet, 4), 4).setCellValue("Total Days");
		getCell(getRow(sheet, 4), 5).setCellValue("Average Allowance");
		getCell(getRow(sheet, 5), 4).setCellFormula("G2-M2");
		getCell(getRow(sheet, 5), 5).setCellFormula("F2/E6");
		
		/*recalculate baseline with exception days*/
		getCell(getRow(sheet, 7), 4).setCellValue("Augmented Base");
		getCell(getRow(sheet, 8), 4).setCellValue("Date");
		getCell(getRow(sheet, 8), 5).setCellValue("Balance");
		
		getRow(sheet, 9).createCell(4).setCellFormula(dateToDateFormula(startingDate)+"-1");
		applyDateFormatting(sheet, 9, 4, dateStyle);
		getRow(sheet, 9).createCell(5).setCellValue(origionalBalance);
		for (int x = 0; x < exceptionDaysRange.length; x++) {
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
        bottomAxis.setTitle("Date"); // https://stackoverflow.com/questions/32010765
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Balance");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        XDDFDataSource<Double> xs1 = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet, new CellRangeAddress(1, 1+summeryAry.length+1, 0, 0));
        XDDFNumericalDataSource<Double> ys1 = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet, new CellRangeAddress(1, 1+summeryAry.length+1, 2, 2));
        
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
		
		String path = new EnviormentVariables().getVariable() + File.separator + "Output.xlsx";	
		FileOutputStream outputStream = new FileOutputStream(path);
		try {
			wb.write(outputStream);
		} catch (IOException e) {
			if (System.console() == null) {
				JOptionPane.showMessageDialog(null, "Could not write to output file " + path +". is it already open by a system viewer/editor?", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		wb.close();
		Desktop.getDesktop().open(new File(path));
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
	
	private static Cell getCell(Row row, int col) {
		Cell temp = row.getCell(col);
			if (temp == null) {
				temp = row.createCell(col);
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
