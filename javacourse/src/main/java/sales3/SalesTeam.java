package sales3;

public class SalesTeam extends SalesUnit {
	// for now use an array, but later a Collection
	private SalesUnit[] members;
	private int memberCt = 0;

	public SalesTeam(String name, double salesTarget) {
		super(name, salesTarget);
		members = new SalesUnit[20];
	}
	public void addMember(SalesUnit salesUnit) {
		if (memberCt >= 20) {
			System.out.println("member limit reached!");
			System.exit(1);
		}
		members[memberCt++] = salesUnit;
	}
	public int getMemberCt() {
		return memberCt;
	}
	public SalesUnit getMember(int index) {
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
