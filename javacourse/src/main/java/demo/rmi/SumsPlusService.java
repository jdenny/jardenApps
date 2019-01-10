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
	4. run SumsPlusService
	5. run SumsPlusGUI
 * @author john.denny@gmail.com
 *
 */
public class SumsPlusService {
	public static void main(String args[]) {
		try {
			SumsPlus sumsPlus = new SumsPlusImpl();
			Naming.rebind("Sums", sumsPlus);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
