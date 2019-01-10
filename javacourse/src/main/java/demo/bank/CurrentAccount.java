package demo.bank;

public class CurrentAccount extends BankAccount {
	private double overdraftLimit = 0.0;

	public CurrentAccount(String name, int number, double overdraftLimit) {
		super(name, number);
		this.setOverdraftLimit(overdraftLimit);
	}
	public CurrentAccount(String name, int number) {
		super(name, number);
	}
	public CurrentAccount(String name) {
		super(name);
	}
	public double getOverdraftLimit() {
		return overdraftLimit;
	}
	public void setOverdraftLimit(double overdraftLimit) {
		this.overdraftLimit = overdraftLimit;
	}
	@Override
	public String toString() {
		return super.toString() + "; overdraftLimit: " +
				overdraftLimit;
	}
	@Override
	public double withdraw(double amount) {
		if (amount > (super.getBalance() + overdraftLimit)) {
			System.out.println("overdraft limit exceeded");
		} else {
			super.deposit(-amount);
		}
		return super.getBalance();
	}

}
