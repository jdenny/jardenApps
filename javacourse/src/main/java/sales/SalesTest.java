package sales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class SalesPersonComparator implements Comparator<SalesPerson> {
	@Override
	public int compare(SalesPerson p1, SalesPerson p2) {
		double sales1 = p1.getSales();
		double sales2 = p2.getSales();
		return Double.compare(sales1, sales2);
	}
}

public class SalesTest {
	public static void main(String[] args) {
		SalesTeam topTeam = SalesMain.buildTeam();
		printTeam(topTeam, 1);
		ArrayList<SalesPerson> salesPersonList = new ArrayList<>();
		getSalesPeople(salesPersonList, topTeam);
		Collections.sort(salesPersonList, new SalesPersonComparator());
		System.out.println("Sales by Person (sorted)");
		for (SalesPerson person: salesPersonList) {
			System.out.println(person.getName() +  ": " + person.getSales());
		}
	}

	private static void getSalesPeople(ArrayList<SalesPerson> salesPersonList,
			SalesTeam team) {
		for (int i = 0; i < team.getMemberCt(); i++) {
			SalesUnit unit = team.getMember(i);
			if (unit instanceof SalesTeam) {
				SalesTeam team2 = (SalesTeam) unit;
				getSalesPeople(salesPersonList, team2);
			} else {
				SalesPerson person = (SalesPerson) unit;
				salesPersonList.add(person);
			}
		}
	}

	private static void printTeam(SalesTeam team, int level) {
		// indentation according to level in hierarchy
		for (int j = 1; j < level; j++) {
			System.out.print("    ");
		}
		System.out.println("Team=" + team);
		for (int i = 0; i < team.getMemberCt(); i++) {
			SalesUnit unit = team.getMember(i);
			if (unit instanceof SalesTeam) {
				printTeam((SalesTeam) unit, level + 1);
			} else {
				for (int j = 1; j < level; j++) {
					System.out.print("    ");
				}
				System.out.println("* " + unit);
			}
		}
	}
}
