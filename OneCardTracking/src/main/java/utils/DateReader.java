package utils;

import java.util.Scanner;

import org.joda.time.LocalDate;

public class DateReader {
	public DateReader() {}
	
	public LocalDate getDateFromUser(String prompt, Scanner sc) {
		System.out.println(prompt);
		String dateInput = sc.nextLine();
		try {
			LocalDate date = LocalDate.parse(dateInput);
			return date;
		} catch (Exception e) {
			System.out.println("Formatting error, please try again");
			return getDateFromUser(prompt, sc);
		}
	}

}
