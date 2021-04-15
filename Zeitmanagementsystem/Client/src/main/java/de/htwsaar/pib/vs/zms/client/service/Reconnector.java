package de.htwsaar.pib.vs.zms.client.service;

/*
 * Singleton-Klasse, die im Hintergrund die Verbindung prüft und die Gui informiert
 */
public class Reconnector implements Runnable {
	
	private final static long TIME_BETWEEN_RECONNECTIONATTEMPTS = 5000;
	private static Reconnector instance;

	/*
	 * Versucht regelmäßig sich erneut mit dem Server zu verbinden
	 */
	@Override
	public void run() {
		while (!GuiMessenger.subscribeToServer()) {
			try {
				Thread.sleep(TIME_BETWEEN_RECONNECTIONATTEMPTS);
			} catch (InterruptedException e) {}
		}
		GuiMessenger.informAboutRestoredConnection();
	}
	
	/*
	 * 
	 */
	public synchronized static Reconnector getInstance() {
		if (instance == null) {
			return new Reconnector();
		}
		return null;
	}
	
	private Reconnector() {
		
	}

}
