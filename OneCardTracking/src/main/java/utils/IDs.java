package utils;

public class IDs {
	
	public static String logInURL = "https://onecard.yu.edu/OneWeb/Account/LogOn";
	public static String logIn_usernameField = "//*[@id=\"oneweb_main_content\"]/div[2]/div[3]/form/fieldset/div[1]/span"; //   //*[@id="Account"]
	public static String logIn_usernameID = "Account";
	public static String logIn_passwordField = "//*[@id=\"Password\"]";///                 //*[@id="Password"]
	public static String logIn_passwordID = "Password";
	public static String logIn_SubmitButton = "//*[@id=\"oneweb_main_content\"]/div[2]/div[3]/form/div/button";
	public static String logIn_errorMessegeXPath = "//*[@id=\"oneweb_main_content\"]/div[2]/div[1]/ul/li";
	
	public static String balancesURL = "https://onecard.yu.edu/OneWeb/Financial/Balances";
	public static String balancesTotal_XPath = "//*[@id=\"oneweb_main_content\"]/div[2]/div[1]/div/div/span";

	
	public static String transactionURL = "https://onecard.yu.edu/OneWeb/Financial/Transactions";
	public static String transactionStartXPath = "//*[@id=\"trans_start_date\"]";
	public static String transactionStartID = "trans_start_date";
	public static String transactionEndXPath = "//*[@id=\"trans_start_date\"]";
	public static String transactionEndID = "trans_end_date";
	public static String transactionSearchButtonXPath = "//*[@id=\"trans_search\"]/span[1]";
	public static String transactionSearchButtomID = "trans_search";
	
	public static String configurationOptions = 
			"Options : -u       ->Set Username\n" +
			"          -p       ->Set Password\n" +
			"          -a       ->Add Exception Days\n" +
			"          -r       ->Remove Exception Days\n"+
			"          -e       ->Return from configuration dialog";
	
}
