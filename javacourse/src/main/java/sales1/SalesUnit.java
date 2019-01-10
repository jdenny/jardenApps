package sales1;

import java.util.Date; // tell compiler where Date class is defined

public class SalesUnit {
	private String name;
	private double salesTarget;
	private Date dateCreated;

	public SalesUnit(String name, double salesTarget) {
		if (salesTarget < 1000) {
			System.out.println("invalid salesTarget: " + salesTarget);
			salesTarget = 1000;
		}
		this.name = name;
		this.salesTarget = salesTarget;
		dateCreated = new Date();
	}
	public String toString() {
		return name + ", target=" + salesTarget + ", " + dateCreated;
	}
	public String getName() {
		return name;
	}
	public double getSalesTarget() {
		return salesTarget;
	}
}
