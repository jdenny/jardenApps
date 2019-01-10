package demo.rmi;

import java.rmi.RemoteException;

public class SumsPlusImpl extends SumsImpl implements SumsPlus, Runnable {
	private static final long serialVersionUID = 1L;
	private SumsListener listener;
	private int waitTime;

	public SumsPlusImpl() throws RemoteException {
		System.out.println("SumsPlusImpl running");
	}
	public void addSumsListener(SumsListener listener, int waitTime) {
		this.listener = listener;
		this.waitTime = waitTime;
		new Thread(this).start();
		return;
	}
	public void run() {
		try {
			Thread.sleep(waitTime * 1000);
			listener.finished("sumsImpl finished waiting " + waitTime);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
