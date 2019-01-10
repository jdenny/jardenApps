package solution;

public class Product {
	private int partNumber;
	private String description;
	private double price;

	public Product(int number, String description, double price) {
		partNumber = number;
		this.description = description;
		this.price = price;
	}
	public String toString() {
		return "Product: " + description + "; partNumber: " + partNumber + "; price: "
			+ price;
	}
	public double getPrice() {
		return price;
	}
	public int getPartNumber() {
		return partNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public static void main(String[] args) {
		Product saw = new Product(123, "Hacksaw", 2.46);
		System.out.println("price = " + saw.getPrice());
		System.out.println(saw.toString());
	}
}
