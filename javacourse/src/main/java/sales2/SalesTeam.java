package sales2;

public class SalesTeam extends SalesUnit {
	private SalesPerson[] members;
	private int memberCt = 0;

	public SalesTeam(String name, double salesTarget) {
		super(name, salesTarget);
		members = new SalesPerson[20];
	}
	public void addMember(SalesPerson member) {
		if (memberCt >= 20) {
			System.out.println("member limit reached!");
			System.exit(1);
		}
		members[memberCt++] = member;
	}
	public int getMemberCt() {
		return memberCt;
	}
	public SalesPerson getMember(int index) {
		return members[index];
	}
	public double getSales() {
		double totalSales = 0.0;
		for (int i = 0; i < memberCt; i++) {
			totalSales += members[i].getSales();
		}
		return totalSales;
	}
	public String toString() {
		return super.toString() + ", sales=" + getSales();
	}
}
