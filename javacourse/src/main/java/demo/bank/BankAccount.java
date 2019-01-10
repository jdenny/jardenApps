package demo.bank;

import java.text.MessageFormat;

public abstract class BankAccount {
	private static int nextNumber = 1;
	private String name;
	private int number;
	private double balance;
	private final static String FORMAT_STR =
		"Account name:{0}; number:{1}; balance:{2,number,0.00}"; 
	public final static String BANK_NAME;
	
	static {
		String retrievedName = "JavaProg European Bank";
		// code to access the file or database;
		BANK_NAME = retrievedName;
	}


	public BankAccount(String name, int number) {
		this.name = name;
		this.number = number;
		this.balance = 0.0;
	}
	
	public BankAccount(String name) {
		this.name = name;
		this.number = nextNumber++;
		this.balance = 0.0;
	}
	public static int getNextNumber() {
		return nextNumber;
	}
	public int getNumber() {
		return this.number;
	}
	public String getName() {
		return this.name;
	}
	public double getBalance() {
		return this.balance;
	}
	public double deposit(double amount) {
		return this.balance += amount;
	}
	public abstract double withdraw(double amount);
	
	@Override
	public String toString() {
		return MessageFormat.format(FORMAT_STR,
				name, number, balance);
		// return name + ", " + number + ", " + balance;
	}
}
