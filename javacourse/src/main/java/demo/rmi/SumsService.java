package demo.rmi;

import java.rmi.Naming;

/**
 * Simple demo of RMI.
 * To run:
	1. open dos window
	2. cd to bin of this project: >cd C:\Users\John\MyEclipse\JavaCourse\bin
	3. start registry: >rmiregistry
	1-3 or:
		run RegistryImpl, in JRE System Library, rt.jar, sun.rmi.registry
	4. run SumsService
	5. run SumsClient
 * @author john.denny@gmail.com
 *
 */
public class SumsService {
	public static void main(String args[]) throws Exception {
		String name = "Sums"; // same as "rmi://localhost:1099/Sums"
		if (args.length == 1) name = args[0];
		Naming.rebind(name, new SumsImpl());
	}
}
