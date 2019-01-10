package sales2;

public class SalesPerson extends SalesUnit {
	private double sales;
	private String photoFile;

	public SalesPerson(String name, double salesTarget, String photo) {
		super(name, salesTarget);
		photoFile = photo;
		sales = 0.0;
	}
	public double addSales(double newSale) {
		return sales += newSale;
	}
	public double getSales() {
		return sales;
	}
	public String getPhotoFile() {
		return photoFile;
	}
	public String toString() {
		return super.toString() + ", " + photoFile + ", sales=" + sales;
	}
}
