package sales3;

public class SalesTest {
	public static void main(String[] args) {
		SalesTeam topTeam = buildTeam();
		printTeam(topTeam, 1);
	}
	public static void printTeam(SalesTeam team, int level) {
		// indentation according to level in hierarchy
		for (int j = 1; j < level; j++) {
			System.out.print("    ");
		}
		System.out.println("Team=" + team);
		for (int i = 0; i < team.getMemberCt(); i++) {
			SalesUnit unit = team.getMember(i);
			if (unit instanceof SalesTeam) {
				printTeam((SalesTeam)unit, level+1);
			} else {
				for (int j = 1; j < level; j++) {
					System.out.print("    ");
				}
				System.out.println("* " + unit);
			}
		}
	}
	public static SalesTeam buildTeam() {
		SalesTeam topTeam = new SalesTeam("ABC Ltd", 20000);
		SalesPerson man2 = new
			SalesPerson("John", 1000, "John.gif");
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
			SalesPerson("Sandra", 6000, "Sandra.gif");
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
