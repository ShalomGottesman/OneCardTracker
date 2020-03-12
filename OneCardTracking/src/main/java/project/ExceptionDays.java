package project;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class ExceptionDays implements Comparable<ExceptionDays>{
	public LocalDate startDate;
	public LocalDate endDate;
	
	public ExceptionDays(LocalDate localDate, LocalDate localDate2){
		this.startDate = localDate;
		this.endDate = localDate2;
		if(localDate.isAfter(localDate2)) {
			throw new IllegalArgumentException();
		}
	}
	
	public int rangeAmount() {
		return Days.daysBetween(startDate, endDate).getDays();
	}

	public int compareTo(ExceptionDays o) {
		if (this.endDate.isBefore(o.startDate)) {
			return -1;
		}
		if (o.endDate.isBefore(this.startDate)) {
			return 1;
		}
		if (this.startDate.isEqual(o.startDate) && this.endDate.isEqual(o.endDate)) {
			return 0;
		}
		throw new IllegalStateException();
	}
	
	public String toString() {
		return this.startDate.toString()+"/"+this.endDate.toString();
	}
}
