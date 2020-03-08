package project;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
			amount  = round(amount, 2);
		}
		return amount;
	}
	
	private static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	public int compareTo(DaySummery o) {
		System.out.println("comp");
		if(this.date.getYear() == o.date.getYear()) {
			if(this.date.getMonthValue() == o.date.getMonthValue()) {
				if(this.date.getDayOfMonth() == o.date.getDayOfMonth()) {
					return 0;
				} else if (this.date.getDayOfMonth() < o.date.getDayOfMonth()) {
					return -1;
				} else {
					return 1;
				}
				
			} else if(this.date.getMonthValue() < o.date.getMonthValue()) {
				return -1;
			} else {
				return 1;
			}
		} else if (this.date.getYear() < o.date.getMonthValue()) {
			return -1;
		} else {
			return 1;
		}
		
		
		/*
		
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
		*/
	}

}