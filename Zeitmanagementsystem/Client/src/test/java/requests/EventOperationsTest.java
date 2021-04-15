package requests;

import java.util.Date;
import java.util.List;

import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.exceptions.UnknownObjectException;
import de.htwsaar.pib.vs.zms.client.exceptions.WrongUserDetailsException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.Repetitiontype;
import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.model.UserRole;

public class EventOperationsTest {
	
	public static void main (String[] args) {
		User root = null;
		try {
			root = ServiceFacade.logIn("root", "rootpw");
		
			Event e = new Event("TestEvent", "Event aus der Testmethode", new Date(), 10, Repetitiontype.NEVER, root);
		
			ServiceFacade.createNewEvent(e);
		
			List<Event> eventlist = ServiceFacade.findCreatedEvents();
			
			for (Event event:eventlist) {
				if (event.getTitle().contentEquals("TestEvent")) {
					e = event;
				}
			}
		
			e.setNote("Aktualisiertes Event aus der Testmethode");
		
			ServiceFacade.saveChangesInEvent(e);
		
			Event refreshed = ServiceFacade.loadEvent(e.getId());
			if (!e.getNote().equals(refreshed.getNote())) {
				throw new IllegalArgumentException("Das event wurde nicht geändert");
			}
			e = refreshed;
			
			User newUser = new User("Udo", "Mustermann", "testUser", UserRole.USER ,"therealudo@web.de");
			newUser.setEnabled(true);
			ServiceFacade.createNewUser(newUser, "Udos_starkes_passwort123");
			
			List<User> list = ServiceFacade.getInvitableUsers(e);
			
			for (User u:list) {
				if (u.getUsername().equals("testUser")) {
					newUser = u;
				}
			}
			
			List<User> users = ServiceFacade.findAllUsers();
			
			User user2 = root = ServiceFacade.logIn("testUser", "Udos_starkes_passwort123");
			
			ServiceFacade.deleteUser(user2);
			
			ServiceFacade.deleteEvent(e);
		
			System.out.println("Hier sollte eine UnknownObjectException stehen");
			ServiceFacade.loadEvent(e.getId());
				
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
