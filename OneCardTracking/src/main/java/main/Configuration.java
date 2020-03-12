package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.joda.time.LocalDate;

import project.ExceptionDays;
import project.config.ProperitesFile;
import utils.DateReader;
import utils.EnviormentVariables;
import utils.IDs;
import utils.PasswordReader;

public class Configuration {
	public static void main(Scanner sc) throws FileNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("-Configuration Dialogue-");
		configLoop(sc);
	}
	
	private static void configLoop(Scanner sc) throws FileNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		File configurationFile = new File(new EnviormentVariables().getVariable()+ File.separator + "configuration.properties");
		ProperitesFile props = new ProperitesFile(configurationFile);
		System.out.println("\tCurrent configuration:");
		System.out.println("\tUsername: " + props.getUsername());
		String password = props.getEncryptedPassword();
		if(password == null || password.equals("")) {
			System.out.println("\tpassword on file: False");
		} else {
			System.out.println("\tpassword on file: True");
		}
		System.out.println("\tStart date: " + props.getStartDate().toString());
		System.out.println("\tEnd date: " + props.getEndDate().toString());
		System.out.println("\tNumber of Exception day sets: " + props.getAmountOfExceptionDays());
		ExceptionDays[] excAry = props.getAllExceptionDays();
		for(int x = 0; x < excAry.length; x++) {
			System.out.println("\t\tException day " + (x+1) + ": " + excAry[x].toString());
		}
		System.out.println("***************************");
		System.out.println(IDs.configurationOptions);
		System.out.print("OneCardTracker>");
		String input = sc.nextLine();
		if (input.contains("-u")) {
			System.out.println("enter your username and press enter");
			System.out.print("Username: ");
			props.setUsername(sc.nextLine());
		}
		if (input.contains("-p")) {
			PasswordReader pr = new PasswordReader();
			props.encryptAndStorePassword(pr.readPassword(sc));
		}
		if (input.contains("-a")) {
			System.out.println("How many exception day sets would you like to add?");
			System.out.print("OneCardTracker> ");
			int setsToAdd = Integer.parseInt(sc.nextLine());
			DateReader dr = new DateReader();
			for (int x = 0; x < setsToAdd; x++) {
				String startPrompt = "Exception Days [" + (x+1) + "] start date, please format as yyyy-mm-dd";
				String endPrompt = "Exception Days [" + (x+1) + "] end date, please format as yyyy-mm-dd";
				LocalDate startDate = dr.getDateFromUser(startPrompt, sc);
				LocalDate endDate = dr.getDateFromUser(endPrompt, sc);
				ExceptionDays exc = new ExceptionDays(startDate, endDate);
				props.addExceptionDays(exc);
			}
		}
		if (input.contains("-r")) {
			System.out.println("Index of the Exception Days to remove");
			System.out.print("index: ");
			int index = Integer.parseInt(sc.nextLine());
			props.removeExceptionDays(index);
		}
		if (input.contains("-e")) {
			return;
		}
		configLoop(sc);
	}
}
