package demo.jdk5;

import java.text.MessageFormat;
import java.util.Date;

public class Formatting {
	private String name;
	private int number;
	private double salesTarget;
	private Date dateCreated;
	private boolean male;

	public static void main(String[] args) {
		Formatting sally = new Formatting("Sally", 23, 44.5, false); 
		System.out.println(sally);
		System.out.println(sally.toString2());
		System.out.println(sally.toString3());
	}

	public Formatting(String name, int number, double salesTarget,
			boolean male) {
		this.name = name;
		this.number = number;
		this.salesTarget = salesTarget;
		this.dateCreated = new Date();
		this.male = male;
	}

	@Override
	public String toString() {
		return "name=" + name + ", number=" + number
				+ ", target=£" + salesTarget + ", dateCreated="
				+ dateCreated + ", male=" + male;
	}
	public String toString2() {
		return MessageFormat.format(
				"name={0}, number={1,number}, target=£{2,number,0.00}, dateCreated={3, date}, male={4}",
				name, number, salesTarget, dateCreated, male);
	}
	public String toString3() {
		return String.format("name=%s, number=%d, target=£%01.2f, dateCreated=%tD, male=%b%n",
			name, number, salesTarget, dateCreated, male);
	}
}
