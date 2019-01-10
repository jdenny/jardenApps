package solution;

class SuppliedProduct extends Product {
	String supplier;
	double cost;

	public SuppliedProduct(int number, String description,
	double price, String supplier, double cost) {
		super(number, description, price);
		this.supplier = supplier;
		this.cost = cost;
	}
	public String toString() {
		return super.toString() + ", " + supplier + ", " + cost +
			", profit: " + getProfit();
	}
	public double getProfit() {
		return getPrice() - cost;
	}
	public static void main(String[] args) {
		Product saw = new Product(123, "Hacksaw", 2.45);
		System.out.println("price = " + saw.getPrice());
		System.out.println(saw.toString());
		SuppliedProduct hammer = new SuppliedProduct(254, "Hammer",
			3.50, "Hawkins Ltd", 1.85);
		System.out.println("profit = " + hammer.getProfit());
		System.out.println("hammer: " + hammer);
	}
}
