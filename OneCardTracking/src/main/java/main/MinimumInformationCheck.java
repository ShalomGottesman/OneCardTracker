package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

import org.joda.time.LocalDate;

import project.config.ProperitesFile;
import utils.DateReader;
import utils.EnviormentVariables;
import utils.PasswordReader;

public class MinimumInformationCheck {
	public static void main(Scanner sc) throws FileNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, URISyntaxException, InterruptedException {
		EnviormentVariables env = new EnviormentVariables();
		File configurationFile = new File(env.getVariable()+ File.separator + "configuration.properties");
		ProperitesFile props = new ProperitesFile(configurationFile);
		String username = props.getUsername();
		String encryptedPassword = props.getEncryptedPassword();
		if (username == null || username.equals("")) {
			if (System.console() == null) {
				JOptionPane.showMessageDialog(null, "Need terminal to configure application, please run OneCardTracker from the terminal with java -jar <pathToJarFile>", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			System.out.println("No user name on file, please input");
			System.out.print("username: ");
			String usernameIn = sc.nextLine();
			props.setUsername(usernameIn);
		}
		if (encryptedPassword == null || encryptedPassword.equals("")) {
			if (System.console() == null) {
				JOptionPane.showMessageDialog(null, "Need terminal to configure application, please run OneCardTracker from the terminal with java -jar <pathToJarFile>", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			PasswordReader pr = new PasswordReader();
			props.encryptAndStorePassword(pr.readPassword(sc));
		}
		DateReader dr = new DateReader();
		if(props.getStartDate() == null) {
			if (System.console() == null) {
				JOptionPane.showMessageDialog(null, "Need terminal to configure application, please run OneCardTracker from the terminal with java -jar <pathToJarFile>", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			LocalDate startDate = dr.getDateFromUser("No start date on file, please type the date of the first day you intend to start spending money on the card in the format yyyy-mm-dd", sc);
			props.setStartDate(startDate);
		}
		if (props.getEndDate() == null) {
			if (System.console() == null) {
				JOptionPane.showMessageDialog(null, "Need terminal to configure application, please run OneCardTracker from the terminal with java -jar <pathToJarFile>", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			LocalDate startDate = dr.getDateFromUser("No end date on file, please type the date of the first day you intend to start spending money on the card in the format yyyy-mm-dd", sc);
			props.setEndDate(startDate);
		}
	}
}
