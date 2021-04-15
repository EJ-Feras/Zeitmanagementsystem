package de.htwsaar.pib.vs.zms.client.serviceMessages;

public interface ZmsObserver {

	public void handleChangeInObjects(ZmsEvent event);
		
	public void handleReconnectionToServer();
}
