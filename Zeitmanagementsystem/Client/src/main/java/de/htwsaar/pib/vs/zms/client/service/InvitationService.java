package de.htwsaar.pib.vs.zms.client.service;

import java.util.ArrayList;
import java.util.List;

import de.htwsaar.pib.vs.zms.client.exceptions.*;
import de.htwsaar.pib.zms.server.model.*;

public class InvitationService {
	
	/*
	 * Gibt alle Benutzer zurück, die zu einem Termin eingeladen werden können
	 */
	public static List<User> getInvitableUsers(Event event) throws NoConnectionToServerException{
		return RequestService.getInstance().getAllUsers();
	}
	
	/*
	 * Lädt einen Benutzer zu einem Termin ein
	 * @param inviter Einladender User
	 * @param invited einzuladender User
	 * @param event Termin
	 */
	public static boolean inviteToEvent(User inviter, List<User> invited, Event event) throws NoConnectionToServerException {
		
			Notification n = Notification.createInvitation(inviter, event);
			n.setReceivers(invited);
			RequestService.getInstance().postNotification(n);
			return true;
	}
	
	public static boolean inviteToEvent(User activeUser, User user, Event event) throws NoConnectionToServerException {
		List<User> userList = new ArrayList<User>();
		userList.add(user);
		return inviteToEvent(activeUser,userList , event);
	}
	
	/*
	 * Akzeptiert die Einladung eines Benutzers
	 * @param user User, der annehmen soll
	 * @param invitation Einladung, die angenommen werden soll
	 * @return Boolean, ob erfolgreich
	 */
	public static boolean acceptInvitation(User user, Notification invitation) throws NoConnectionToServerException {
		RequestService server = RequestService.getInstance();
		Notification n = Notification.createAcceptance(user, invitation);
		n.setReceiver(invitation.getSender());
		
		server.postNotification(n);
		return true;
	}
	
	/*
	 * Lehnt die Einladung eines Benutzers ab
	 * @param user User, der ablehnen soll
	 * @param invitation Einladung, die abgelehnt werden soll
	 * @return Boolean, ob erfolgreich
	 */
	public static boolean declineInvitation(User user, Notification invitation) throws NoConnectionToServerException {
		RequestService server = RequestService.getInstance();
		Notification n = Notification.createRejection(user, invitation);
		n.setReceiver(invitation.getSender());
		
		server.postNotification(n);
		return true;
	}
	
}
