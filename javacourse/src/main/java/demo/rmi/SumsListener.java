package demo.rmi;

import java.rmi.*;

public interface SumsListener extends Remote {
	public void finished(String message) throws RemoteException;
}
