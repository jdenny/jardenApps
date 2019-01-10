package solution;

public class Variables {
	public static void main(String[] args) {
		// part 1:
		int width;
		int height = 40;
		boolean animated = false;
		double cost;
		String name = "John";

		width = 15;
		cost = 1.23e-4;
		System.out.println("width=" + width);
		System.out.println("height=" + height);
		System.out.println("animated=" + animated);
		System.out.println("cost=" + cost);
		System.out.println("name=" + name);
		// part 2:
		width += 23;
		name = "Paul";
		name += " and George";
		animated = (width > 30);
		cost = width * 3.56;
		System.out.println("width=" + width);
		System.out.println("height=" + height);
		System.out.println("animated=" + animated);
		System.out.println("cost=" + cost);
		System.out.println("name=" + name);
	}
}
