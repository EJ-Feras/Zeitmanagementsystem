package receiveServerSentEvents;

import java.util.Date;
import java.util.List;

import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.serviceMessages.Change;
import de.htwsaar.pib.vs.zms.client.serviceMessages.Modelclass;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsEvent;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsObserver;
import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.Repetitiontype;
import de.htwsaar.pib.zms.server.model.User;

public class ServerEventsTest {
	
	public static boolean received;
	
	public static void main(String[] args) {
		
		try {
			User root = ServiceFacade.logIn("root", "rootpw");
			
			ZmsObserver observer = new ZmsObserver(){
	
				@Override
				public void handleChangeInObjects(ZmsEvent event) {
					System.out.println("Server-Sent-Event erhalten: " + event.getId() + "," + event.getChangetype() + "," + event.getModelClass());	
					if (event.getChangetype().equals(Change.update) && event.getModelClass().equals(Modelclass.Event)) {
						received = true;
					}
				}
	
				@Override
				public void handleReconnectionToServer() {
					// TODO Auto-generated method stub
					
				}
		};
		ServiceFacade.subscribeToServerEvents(observer);
		
		Event event = new Event("TestEvent", "Event aus der Testmethode", new Date(), 10, Repetitiontype.NEVER, root);
		ServiceFacade.createNewEvent(event);
					
		List<Event> eventlist = ServiceFacade.findCreatedEvents();
		
		for (Event e:eventlist) {
			if (e.getTitle().contentEquals("TestEvent")) {
				event = e;
			}
		}
	
		event.setNote("Aktualisiertes Event aus der Testmethode");
		
		ServiceFacade.saveChangesInEvent(event);
		
		Thread.sleep(500);
		
		ServiceFacade.deleteEvent(event);
		
		System.out.println("Testergebnis: " + received);
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}
}
