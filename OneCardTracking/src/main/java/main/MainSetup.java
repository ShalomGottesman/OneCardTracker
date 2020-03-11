package main;

import java.io.File;
import java.io.IOException;

import utils.EnviormentVariables;

public class MainSetup {
	public static void main(String[] args) {
		PreCheck.main(null);
		if(args.length == 0) {
			runAuto();
			return;
		} 
		if(args.length == 1 && args[0].equals("config")) {
			runConfiguration();
		}
	}
	
	static void runConfiguration() throws IOException {
		
	}
}
