package demo.rmi;

import java.rmi.Naming;

/**
 * Simple demo of RMI.
 * To run: see SumsService
 * @author john.denny@gmail.com
 *
 */
public class SumsClient {

	public static void main(String[] args) throws Exception {
		System.out.println("list of objects bound to rmiregistry:");
		String[] names = Naming.list("rmi://localhost:1099");
		for (String name: names) {
			System.out.println(name);
		}
		System.out.println("end of list");
		String name = "rmi://localhost:1099/Sums";
		if (args.length == 1) name = args[0];
		Sums sums = (Sums)Naming.lookup(name);
		System.out.println("5 + 3 = " + sums.add(5, 3));
		System.out.println("5 - 63 = " + sums.subtract(5, 63));
		System.out.println("next = " + sums.next());
	}
}
