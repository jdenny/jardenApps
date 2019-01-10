package demo.bank;

import java.io.Serializable;

public class SpecialAccount extends BankAccount implements MessageListener,
			Persistent, Serializable {

	private static final long serialVersionUID = 1L;
	private double overdraftLimit;

	public SpecialAccount(String name) {
		super(name);
	}
	@Override
	public void save() {
		System.out.println("save method not yet implemented!");
	}

	@Override
	public void load() {
		System.out.println("load method not yet implemented!");
	}

	@Override
	public void onMessage(String message) {
		System.out.println("SpecialAccount.onMessage(" + message + ")");
	}

	@Override
	public double withdraw(double amount) {
		if (amount > (super.getBalance() + overdraftLimit)) {
			System.out.println("he's a very naughty boy");
		}
		return super.deposit(-amount);
	}

}
