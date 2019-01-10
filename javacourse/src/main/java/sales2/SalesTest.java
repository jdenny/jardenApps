package sales2;

public class SalesTest {
	public static void main(String[] args) {
		SalesTeam topTeam = buildTeam();
		System.out.println("topTeam=" + topTeam);
		System.out.println("members of team " + topTeam.getName());
		for (int i = 0; i < topTeam.getMemberCt(); i++) {
			System.out.println("* " + topTeam.getMember(i));
		}
	}
	private static SalesTeam buildTeam() {
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
		return topTeam;
	}
}
