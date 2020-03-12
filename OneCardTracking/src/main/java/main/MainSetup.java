package main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import project.searchAndWrite.MainPropject;
import utils.IDs;
import utils.InternetConnection;

public class MainSetup {
	public static void main(String[] args) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, URISyntaxException, InterruptedException {
		FilesCheck.main(null);
		System.out.println("File check complete");
		Scanner sc = new Scanner(System.in);
		MinimumInformationCheck.main(sc);
		System.out.println("Minimum information check complete");
		if(new InternetConnection(new URL(IDs.logInURL)).isConnectionAvailable()) {
			System.out.println("internet conection available");
		} else {
			System.out.println("Could not connect to onecard website, are you connected to the internet?");
			System.exit(1);
		}
		if(args.length == 0) {
			MainPropject.main();
			return;
		} 
		if(args.length == 1 && args[0].equals("config")) {
			Configuration.main(sc);
			return;
		}
	}

}
