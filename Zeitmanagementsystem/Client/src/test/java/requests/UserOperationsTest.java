package requests;

import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.exceptions.UnknownObjectException;
import de.htwsaar.pib.vs.zms.client.exceptions.WrongUserDetailsException;
import de.htwsaar.pib.vs.zms.client.service.RequestService;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.model.UserRole;

public class UserOperationsTest {
	
	public static void main (String[] args) {
		User root = null;
		try {
			root = ServiceFacade.logIn("root", "rootpw");
		} catch (UnknownObjectException | WrongUserDetailsException | NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		User newUser = new User("Udo", "Mustermann", "umus", UserRole.USER ,"therealudo@web.de");
		try {
			ServiceFacade.createNewUser(newUser, "Udos_starkes_passwort123");
		} catch (NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			newUser = ServiceFacade.logIn("umus", "Udos_starkes_passwort123");
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
		
		newUser.setFirstName("ursula");
		
		try {
			ServiceFacade.saveUpdatedUser(newUser);
		} catch (NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			newUser = ServiceFacade.loadCurrentUser();
		} catch (NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("User's name is now " + newUser.getFirstName());
		
		try {
			ServiceFacade.deleteUser(newUser);
		} catch (NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			newUser = ServiceFacade.loadCurrentUser();
		} catch (NoConnectionToServerException e) {
			
		}
		System.out.println("Hierüber sollte eine WrongUserDetailsException sein");
	}
	
}
