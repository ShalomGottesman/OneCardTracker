package project;

import java.time.LocalDateTime;

public class Transaction {
	public LocalDateTime timeStamp;
	public double amount;
	
	Transaction(LocalDateTime stamp, double amount){
		this.timeStamp = stamp;
		this.amount = amount;
	}

}
