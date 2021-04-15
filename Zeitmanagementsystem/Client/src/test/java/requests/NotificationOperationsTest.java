package requests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Date;
import java.util.List;

import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.exceptions.UnknownObjectException;
import de.htwsaar.pib.vs.zms.client.exceptions.WrongUserDetailsException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.Notificationtype;
import de.htwsaar.pib.zms.server.model.Repetitiontype;
import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.model.UserRole;

public class NotificationOperationsTest {
	
	public static void main (String[] args) {
		User currentUser = null;
		Event event;
		
		try {
			currentUser = ServiceFacade.logIn("root", "rootpw");
		
			event = new Event("TestEvent", "Event aus der Testmethode", new Date(), 10, Repetitiontype.NEVER, currentUser);

			ServiceFacade.createNewEvent(event);

			List<Event> eventlist = null;
			eventlist = ServiceFacade.findCreatedEvents();
		
			User newUser = new User("Udo", "Mustermann", "testUser", UserRole.USER ,"therealudo@web.de");
			ServiceFacade.createNewUser(newUser, "Udos_starkes_passwort123");

			for (Event e:eventlist) {
				if (e.getTitle().equals("TestEvent")){
					event = e;
				}
			}
			
			List<User> invitables = ServiceFacade.getInvitableUsers(event);
			
			for (User u:invitables) {
				if (u.getUsername().equals("testUser")) {
					newUser = u;
				}
			}
		
			ServiceFacade.inviteToEvent(newUser, event);
			
			currentUser = ServiceFacade.logIn("testUser", "Udos_starkes_passwort123");
			
			Notification invitation;
			try {
				invitation = ServiceFacade.findLastNotifications(1).get(0);
			}
			catch (java.lang.IndexOutOfBoundsException ex) {
				throw new Exception("Notification nicht gefunden");
			}
			
			if (invitation.getNotificationtype() == Notificationtype.invitation) {
				ServiceFacade.acceptInvitation(invitation);
			}
			else {
				throw new Exception("Einladung nicht erhalten");
			}
			
			currentUser = ServiceFacade.logIn("root", "rootpw");
		
			Notification acceptance = ServiceFacade.findLastNotifications(1).get(0);
			
			if (acceptance.getNotificationtype() != Notificationtype.acceptance) {
				throw new Exception("Bestätigung nicht erhalten");
			}
			
			if (acceptance.getEvent().getParticipants().size() == 0) {
				throw new Exception("Benutzer nicht hinzugefügt");
			}
			
			currentUser = ServiceFacade.logIn("testUser", "Udos_starkes_passwort123");
			
			eventlist = ServiceFacade.findUpcomingEvents();
			
			boolean found = false;
			for (Event e: eventlist) {
				if (e.getTitle().equals("TestEvent")) {
					found = true;
				}
			}
			
			if (!found) {
				throw new Exception("Event nicht im User");
			}
			
			ServiceFacade.deleteEvent(event);
			
			ServiceFacade.deleteUser(currentUser);
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
	
