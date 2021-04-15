package receiveEventsExample;

import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.serviceMessages.Change;
import de.htwsaar.pib.vs.zms.client.serviceMessages.Modelclass;
import de.htwsaar.pib.vs.zms.client.exceptions.*;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsEvent;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsObserver;
import de.htwsaar.pib.zms.server.model.Notification;

public class example {
	
	private void handleEvent(ZmsEvent e) {
		if (e.getModelClass() == Modelclass.Event) {
			if (e.getChangetype() == Change.deletedObject){
				//handling
			}
			if (e.getChangetype() == Change.update) {
				//handling
			}
			if (e.getChangetype() == Change.newObject) {
				//handling
			}
		}
		else if (e.getModelClass() == Modelclass.User) {
			if (e.getChangetype() == Change.update) {
				//handling
			}
		}
		else if (e.getModelClass() == Modelclass.Notification) {
			if (e.getChangetype() == Change.newObject) {
				try {
					try {
						Notification newNotification = ServiceFacade.loadNotification(e.getId());
					} catch (NoConnectionToServerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (UnknownObjectException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void subscribe() {
		ZmsObserver observer = new ZmsObserver(){

				@Override
				public void handleChangeInObjects(ZmsEvent event) {
					handleEvent(event);					
				}

				@Override
				public void handleReconnectionToServer() {
					// TODO Auto-generated method stub
					
				}
		};
		ServiceFacade.subscribeToServerEvents(observer);
	}	
}
