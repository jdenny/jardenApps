package solution.enums;

public enum DayOfWeek {
	SUNDAY,
	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY,
	SATURDAY;
	
	public DayOfWeek getNext() {
		int tomorrowIndex = this.ordinal() + 1;
		if (tomorrowIndex >= DayOfWeek.values().length) tomorrowIndex = 0;
		return DayOfWeek.values()[tomorrowIndex];
	}
}

