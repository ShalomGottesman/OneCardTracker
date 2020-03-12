package project;

import org.joda.time.LocalDateTime;

public class Transaction {
	public LocalDateTime timeStamp;
	public double amount;
	
	public Transaction(LocalDateTime stamp, double amount){
		this.timeStamp = stamp;
		this.amount = amount;
	}

}
