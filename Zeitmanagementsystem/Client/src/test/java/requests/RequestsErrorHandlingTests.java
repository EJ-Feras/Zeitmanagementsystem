package requests;

import de.htwsaar.pib.vs.zms.client.service.RequestService;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.exceptions.*;
import de.htwsaar.pib.zms.server.model.*;

/*
 * Ausführen, ohne Server zu starten. Testet die vier HTTP-Operationen einmal ohne Serververbindung
 */
public class RequestsErrorHandlingTests {
	
	public static void main(String[] args){
		
		User u = null;
		Event ev = null;
		
			try {
				u = ServiceFacade.logIn("unbekannterUser", "schlechtesPasswort");
			} catch (UnknownObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongUserDetailsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoConnectionToServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			ev = new Event();
			try {
				System.out.println(ServiceFacade.saveChangesInEvent(ev));
			} catch (NoConnectionToServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

			ev = new Event();
			try {
				System.out.println(RequestService.getInstance().postEvent(ev));
			} catch (NoConnectionToServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			u = new User("Bob", "der", "Baumeister", "baut", "einen");
			Notification notification = Notification.createInvitation(u, ev);
			try {
				System.out.println(RequestService.getInstance().deleteNotification(notification));
			} catch (NoConnectionToServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	
}
