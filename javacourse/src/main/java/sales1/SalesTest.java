package sales1;

public class SalesTest {
	public static void main(String[] args) {
		SalesUnit teamA = new SalesUnit("A Team", 2000);
		System.out.println("Team = " + teamA);
		SalesUnit teamB = new SalesUnit("Dirty Tricks Unit", 25);
		System.out.println("name of teamB = " + teamB.getName());
		// teamA.salesTarget = 200; // compilation error
	}
}
