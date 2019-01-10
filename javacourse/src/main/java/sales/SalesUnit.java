package sales;

import java.util.Date;

import jarden.gui.SalesUnitIF;

public abstract class SalesUnit implements SalesUnitIF {
	private String name;
	private double salesTarget;
	private Date dateCreated;

	public SalesUnit(String name, double salesTarget) {
		if (salesTarget < 1000) {
			throw new IllegalArgumentException("salesTarget is " +
				salesTarget + " but must be at least 1000");
		}
		this.name = name;
		this.salesTarget = salesTarget;
		dateCreated = new Date();
	}
	public String toString() {
		return String.format("%s, target=£%01.2f, dateCreated=%tF",
				name, salesTarget, dateCreated);
	}
	public String getName() {
		return name;
	}
	public double getSalesTarget() {
		return salesTarget;
	}
	public abstract double getSales();
}
