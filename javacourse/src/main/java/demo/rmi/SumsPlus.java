package demo.rmi;

import java.rmi.*;

/**
 * Extension to basic RMI example Sums. This adds a new method
 * which notifies a SumsListener after a specified number of seconds.
 * Note that SumsListener is itself a Remote interface.
 */
public interface SumsPlus extends Sums {
	public void addSumsListener(SumsListener listener, int waitTimeSeconds)
		throws RemoteException;
}
