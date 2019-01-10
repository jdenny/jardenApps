package demo.rmi;

import java.rmi.*;

public interface Sums extends Remote {
	public int add(int a, int b) throws RemoteException;
	public int subtract(int a, int b) throws RemoteException;
	public int next() throws RemoteException;
}
