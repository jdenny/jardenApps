package demo.bank;

public class DepositAccount extends BankAccount {
	private double interestRate;

	public DepositAccount(String name, int number, double rate) {
		super(name, number);
		this.interestRate = rate;
	}
	public double addInterest() {
		double interest = super.getBalance() * this.interestRate;
		return super.deposit(interest);
	}
	public void setRate(double newRate) {
		interestRate = newRate;
	}
	@Override
	public String toString() {
		return super.toString() + "; interestRate: " +
				interestRate;
	}
	@Override
	public double withdraw(double amount) {
		if (amount > super.getBalance()) {
			System.out.println("insufficient funds available");
		} else {
			super.deposit(-amount);
		}
		return super.getBalance();
	}
}
