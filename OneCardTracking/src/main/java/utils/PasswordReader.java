package utils;

import java.util.Scanner;

public class PasswordReader {
	public PasswordReader(){}
	
	public String readPassword(Scanner sc) {
		String password = "";
		java.io.Console console = System.console();
		if (console != null) {
			System.out.println("No password on file, please input. Be aware that your password will NOT appear on screen as you type");
			char[] pwd = console.readPassword("password: ");
			password = new String(pwd);
		} else {
			System.out.println("could not get instance of System console, password will be printed");
			System.out.print("password: ");
			password = sc.nextLine();
		}
		return password;
	}
}
