package solution.enums;

public enum Country {
	ENGLAND("England", "London"),
	SCOTLAND("Scotland", "Edinburgh"),
	WALES("Wales", "Cardiff"),
	IRELAND("Ireland", "Dublin"),
	NORTHERN_IRELAND("Northern Ireland", "Belfast");
	
	private final String name;
	private final String capital;
	
	private Country(String name, String capital) {
		this.name = name;
		this.capital = capital;
	}
	public String getName() {
		return this.name;
	}
	public String getCapital() {
		return this.capital;
	}
	public boolean isCapital(String capital) {
		return this.capital.equalsIgnoreCase(capital);
	}
}

