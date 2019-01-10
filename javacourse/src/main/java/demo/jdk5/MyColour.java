package demo.jdk5;

public enum MyColour {
	RED(255, 0, 0),
	GREEN(0, 255, 0),
	BLUE(0, 0, 255),
	BROWN(0xFFFF00),
	PURPLE(0xFF00FF),
	TURQUOISE(0xFFFF),
	BLACK(0),
	WHITE(0xFFFFFF);
	
	private int red;
	private int green;
	private int blue;
	
	private MyColour(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	private MyColour(int rgb) {
		this.red = rgb >> 16;
		this.green = rgb >> 8 & 0xFF;
		this.blue = rgb & 0xFF;
	}
	public String toString() {
		return "Colour " + this.name() + "(" + this.ordinal() + ") [" +
				red + "," + green + "," + blue + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyColour[] allColours = MyColour.values();
		for (MyColour colour: allColours) {
			System.out.println(colour);
		}
		System.out.println("adios amigo");
	}

}
