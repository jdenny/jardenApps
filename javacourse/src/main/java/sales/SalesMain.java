package sales;

import jarden.gui.SalesSwing;

public class SalesMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SalesSwing(buildTeam());
	}
	/**
	 * This is a convenience method to set up some objects for the
	 * sales application. Ideally it would be replaced by a persistent
	 * version, either a RDBMS table, and a utility class to manage it,
	 * or an XML file with a utility class to manage it.
	 */
	public static SalesTeam buildTeam() {
		SalesTeam topTeam = new SalesTeam("ABC Ltd", 20000);
		SalesPerson man2 = new
			SalesPerson("Joe", 1000, "Joe.gif");
		SalesPerson woman3 = new
			SalesPerson("Jean", 2000, "Jean.gif");
		SalesPerson woman4 = new
			SalesPerson("Julie", 3000, "Julie.gif");
		topTeam.addMember(man2);
		topTeam.addMember(woman3);
		topTeam.addMember(woman4);
		man2.addSales(100.0);
		woman3.addSales(200.0);
		woman4.addSales(300.0);
		SalesTeam team5 = new SalesTeam("XYZ", 10000);
		SalesPerson man6 = new
			SalesPerson("Simon", 4000, "Simon.gif");
		SalesPerson woman7 = new
			SalesPerson("Sam", 5000, "Sam.gif");
		SalesPerson woman8 = new
			SalesPerson("Sandy", 6000, "Sandra.gif");
		team5.addMember(man6);
		team5.addMember(woman7);
		team5.addMember(woman8);
		man6.addSales(200.0);
		woman7.addSales(400.0);
		woman8.addSales(600.0);
		topTeam.addMember(team5);
		return topTeam;
	}
}
