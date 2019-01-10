package sales;

import jarden.gui.SalesTeamIF;

import java.util.ArrayList;

public class SalesTeam extends SalesUnit implements SalesTeamIF {
	private ArrayList<SalesUnit> members;

	public SalesTeam(String name, double salesTarget) {
		super(name, salesTarget);
		members = new ArrayList<SalesUnit>();
	}
	public void addMember(SalesUnit salesPerson) {
		members.add(salesPerson);
	}
	public int getMemberCt() {
		return members.size();
	}
	public SalesUnit getMember(int index) {
		return members.get(index);
	}
	public double getSales() {
		double totalSales = 0.0;
		for (int i = 0; i < members.size(); i++) {
			SalesUnit member = members.get(i);
			totalSales += member.getSales();
		}
		return totalSales;
	}
	public String toString() {
		return super.toString() + ", sales=" + getSales();
	}
}
