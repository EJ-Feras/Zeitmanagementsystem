package de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller;

import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsEvent;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsObserver;

public class MyZmsObserver implements ZmsObserver, Runnable {
	
	private MainViewController controller;
	private ZmsEvent event;
	
	public MyZmsObserver( MainViewController m) {
		this.controller = m;
	}

	@Override
	 public void handleChangeInObjects(ZmsEvent event) {
		this.event = event;
		run();
	 }
	
	 @Override
	 public void handleReconnectionToServer() {

	}

	@Override
	public void run() {
		controller.handleEvent(event);	
	}

}
