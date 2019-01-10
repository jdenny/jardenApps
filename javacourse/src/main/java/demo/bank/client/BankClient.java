package demo.bank.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import demo.bank.BankAccount;
import demo.bank.CurrentAccount;
import demo.bank.DepositAccount;
import demo.bank.MessageListener;
import demo.bank.Persistent;
import demo.bank.SpecialAccount;

public class BankClient {
	public static void main(String[] args) {
		// declare variables:
		double amount;
		BankAccount acc1;

		// create a new instance of class BankAccount:
		// acc1 = new BankAccount(); // compiler error...
		// ...as constructor parameters not supplied
		// balance = acc1.balance; // balance private
		// acc1 = null;
		// acc1.deposit(22.75); // runtime error, as acc1 null
		
		// initialise variables:
		amount = 60.5;
		acc1 = new CurrentAccount("Jack", 6723);
		acc1.deposit(123.45);
		// pass variables as parameters:
		makeDeposit(acc1, amount);
		System.out.println("balance " + acc1.getBalance());
		System.out.println("acc1=" + acc1);
		polyExample();
		bankArray();
		statics();
		bankCollectionNoGenerics();
		bankCollection();
		multiInterfaces();
	}
	public static void makeDeposit(BankAccount account, double amount) {
		double newBalance = account.deposit(amount);
		System.out.println(amount + " deposited in account " +
				account.getName() + "; new balance=" + newBalance);
	}
	public static void polyExample() {
		DepositAccount depAcc = new DepositAccount(
			"JDL", 5379, 0.003);
		CurrentAccount currAcc = new CurrentAccount("Sam", 4458);
		System.out.println(depAcc); // DepositAccount's toString()
		System.out.println(currAcc); // CurrentAccount toString()
		BankAccount account;
		account = currAcc;
		System.out.println(account); // CurrentAccount toString()
		account = depAcc;
		System.out.println(account); // DepositAccount's toString()
		account = new DepositAccount("Julie", 9943, 0.004);
		System.out.println(account); // DepositAccount's toString()
		System.out.println("Bank: " + BankAccount.BANK_NAME);
		// BankAccount.BANK_NAME = "SeeSharp World Bank";
	}
	public static void bankArray() {
		BankAccount first =
			new CurrentAccount("first account", 1001, 250);
		BankAccount second =
			new DepositAccount("second account", 2002, 0.005);
		BankAccount[] accounts;
		accounts = new BankAccount[3];
		accounts[0] = first;
		accounts[1] = second;
		accounts[2] = new CurrentAccount("third account", 1003);
		printAllAccounts(accounts);
		incrementOverdraftLimits(accounts, 1.1);
		printAllAccounts(accounts);
	}
	public static void printAllAccounts(BankAccount[] accounts) {
		int currentCount = 0;
		System.out.println("all accounts:");
		for (BankAccount account: accounts) {
			System.out.println("  " + account);
			if (account instanceof CurrentAccount) {
				currentCount++;
			}
		}
		System.out.println("number of current accounts: " + currentCount);
	}
	public static void incrementOverdraftLimits(BankAccount[] accounts, double factor) {
		for (BankAccount account: accounts) {
			if (account instanceof CurrentAccount) {
				// double odLimit = account.getOverdraftLimit();
				CurrentAccount currAcc = (CurrentAccount)account;
				double odLimit = currAcc.getOverdraftLimit();
				currAcc.setOverdraftLimit(odLimit * factor);
			}
		}
	}
	public static void statics() {
		BankAccount acc1 = new CurrentAccount("john");
		int number = acc1.getNumber();
		int nextNumber = BankAccount.getNextNumber();
		System.out.println("acc1.number=" + number + "; nextNumber=" +
				nextNumber);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void bankCollectionNoGenerics() {
		BankAccount first =
			new CurrentAccount("first account", 1001, 250);
		BankAccount second =
			new DepositAccount("second account", 2002, 0.005);
		List accounts = new ArrayList();
		accounts.add(first);
		accounts.add(second);
		accounts.add(new CurrentAccount("third account", 1003));
		System.out.println("bankCollectionNoGenerics, all accounts:");
		for (Object obj: accounts) {
			BankAccount account = (BankAccount)obj;
			String str = account.toString();
			System.out.println("  " + str);
		}
	}
	public static void bankCollection() {
		BankAccount first =
			new CurrentAccount("first account", 1001, 250);
		BankAccount second =
			new DepositAccount("second account", 2002, 0.005);
		List<BankAccount> accounts = new ArrayList<>();
		accounts.add(first);
		accounts.add(second);
		accounts.add(new CurrentAccount("third account", 1003));
		System.out.println("bankCollection, all accounts:");
		for (BankAccount account: accounts) {
			String str = account.toString();
			System.out.println("  " + str);
		}
	}
	public static void multiInterfaces() {
		SpecialAccount special = new SpecialAccount("Terry");
		useListener(special); // special IS-A MessageListener
		persist(special); // special IS-A Persistent
		serialize(special); // special IS-A Serializable
		System.out.println("special is a BankAccount: " + 
				(special instanceof BankAccount));
		BankAccount account = special;
		System.out.println("account is a DepositAccount: " + 
				(account instanceof DepositAccount));
	}
	public static void useListener(MessageListener listener) {
		listener.onMessage("message to listener");
	}
	public static void persist(Persistent persistent) {
		persistent.load();
		persistent.save();
	}
	public static void serialize(Serializable serializable) {
		System.out.println("serializable=" + serializable);
	}
}
