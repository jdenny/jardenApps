package demo.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SumsImpl extends UnicastRemoteObject implements Sums {
	private static final long serialVersionUID = 1L;
	private int series = 0;

	public SumsImpl() throws RemoteException {
		System.out.println("SumsImpl running");
	}
	public int add(int a, int b) {
		return a + b;
	}
	public int subtract(int a, int b) {
		return a - b;
	}
	public int next() {
		return ++series;
	}
}
