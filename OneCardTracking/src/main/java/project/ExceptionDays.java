package project;

import java.time.LocalDate;

public class ExceptionDays implements Comparable<ExceptionDays>{
	LocalDate startDate;
	LocalDate endDate;
	
	ExceptionDays(LocalDate start, LocalDate end){
		this.startDate = start;
		this.endDate = end;
		if(start.isAfter(end)) {
			throw new IllegalArgumentException();
		}
	}
	
	public int rangeAmount() {
		return this.startDate.until(this.endDate).getDays();
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

}
