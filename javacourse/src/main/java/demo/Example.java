package demo;

public class Example {
	int quantity;
	String name;
	double cost;

	public String getName() {
		return name;
	}
	public double getPrice() {
		return Two.calculatePrice(quantity, cost);
	}
	public static void main(String[] args) {
		Example fj = new Example();
		fj.quantity = 4;
		fj.cost = 1.25;
		fj.name= "Wheat-free Flapjack";
		System.out.println("price = " + fj.getPrice());
	}
}

class Two {
	static double profit = 1.4;

	public static double calculatePrice(int num, double cost) {
		return cost * num * profit;
	}
}
