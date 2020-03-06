package project;

import java.time.LocalDate;
import java.util.ArrayList;

public class DaySummery implements Comparable<DaySummery>{
	ArrayList<Transaction> transactions;
	LocalDate date;
	
	DaySummery(LocalDate date){
		this.date = date;
		this.transactions = new ArrayList<Transaction>();
	}
	
	public double summery() {
		double amount = 0;
		for (Transaction transaction : transactions) {
			amount += transaction.amount;
		}
		return amount;
	}

	public int compareTo(DaySummery o) {
		if(this.date.isBefore(o.date)) {
			return -1;
		}
		if (this.date.isEqual(o.date)) {
			return 0;
		}
		if (this.date.isAfter(o.date)){
			return -1;
		}
		return 0;
	}

}
