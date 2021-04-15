package de.htwsaar.pib.vs.zms.client.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.htwsaar.pib.vs.zms.client.exceptions.*;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsObserver;
import de.htwsaar.pib.zms.server.model.*;

public class ServiceFacade {
	
	private static User activeUser;
	
	//User-Requests-----------------------------------------------------------------
	
	/*
	 * Versucht einen Log-in beim Server
	 * @return user Objekt
	 */
	public static User logIn(String username, String password) throws UnknownObjectException, WrongUserDetailsException, NoConnectionToServerException {
		User user = RequestService.getInstance().getUser(username, password);
		activeUser = user;
		GuiMessenger.subscribeToServer();
		return activeUser;
	}
	
	
	/*
	 * Gibt alle User zurück, die noch zum Termin eingeladen werden können
	 * @param event betroffener Termin
	 * @return Liste der User
	 */
	public static List<User> getInvitableUsers(Event event) throws NoConnectionToServerException{
		List<User> result = getInvitableUsers();
		result.removeAll(event.getParticipants());
		return result;
	}
	
	/*
	 * Gibt alle User zurück, die zu Terminen eingeladen werden können
	 * @param event betroffener Termin
	 * @return Liste der User
	 */
	public static List<User> getInvitableUsers() throws NoConnectionToServerException{
		List<User> result = findAllUsers();
		return result;
	}
	
	/*
	 * Sendet dem Server einen neu angelegten Benutzer zum speichern
	 * @param user Benutzer
	 */
	public static User createNewUser(User user, String password) throws NoConnectionToServerException {
		return RequestService.getInstance().postUser(user, password);
	}
	
	/*
	 * Sendet dem Server den aktualisierten Benutzer zum Speichern, Für verändertes Passwort kann password im User neu gesetzt werden
	 * @param user Boolean ob erfolgreich
	 */
	public static User saveUpdatedUser(User user) throws NoConnectionToServerException {
		String password = "";
		if(user.getPassword() != null) {
			password = user.getPassword();
		}
		UserPasswordStruct struct = new UserPasswordStruct(user, password);
		return RequestService.getInstance().putUser(struct);
	}
	
	/*
	 * Lädt den aktuell eingeloggten User erneut
	 * @return User objekt
	 */
	public static User loadCurrentUser() throws NoConnectionToServerException {
		try {
			return RequestService.getInstance().refreshCurrentUser();
		}
		catch (UnknownObjectException e) {
			// Kann nur Auftreten, wenn wir einen Fehler im Programm haben
			return null;
		}
	}
	
	public static boolean deleteUser(User u) throws NoConnectionToServerException {
			return RequestService.getInstance().deleteUser(u);
	}
	
	/*
	 * Gibt alle User zurück
	 * @return Liste aller User
	 */
	public static List<User> findAllUsers() throws NoConnectionToServerException {
		List<User> result =  RequestService.getInstance().getAllUsers();
		int indexOfActiveUser = -1;
		for (int i = 0 ; i <result.size(); i++) {
			User user = result.get(i);
			if (user.getUsername().equals(activeUser.getUsername())) {
				indexOfActiveUser = i;
				break;
			}
		}
	
		if(indexOfActiveUser > -1)
			result.remove(indexOfActiveUser);
		return result;
	}
	
	//Server Sent Events-----------------------------------------------------------
	
	/*
	 * Gibt Observer an ServiceSchicht, um ServerEvents zu empfangen
	 * @param o ZmsObserver mit den Anweisungen
	 */
	public static boolean subscribeToServerEvents(ZmsObserver o) {
		return GuiMessenger.subscribe(o);
	}
	
	//Event-Requests---------------------------------------------------------------
	
	/*
	 * Sendet dem Server einen neu angelegten Termin zum speichern
	 * @param event Termin
	 * @return Boolean ob erfolgreich
	 */
	public static Event createNewEvent(Event event) throws NoConnectionToServerException {
		return RequestService.getInstance().postEvent(event);
	}
	
	/*
	 * Sendet dem Server den aktualisierten Termin zum Speichern
	 * @param event Termin
	 * @return Boolean ob erfolgreich
	 */
	public static Event saveChangesInEvent(Event event) throws NoConnectionToServerException {
		return RequestService.getInstance().putEvent(event);
	}
	
	/*
	 * Lädt das Event mit der gegebenen id
	 * @param id Datenbank-id des Events
	 * @return Event Objekt
	 */
	public static Event loadEvent(long eventId) throws UnknownObjectException, NoConnectionToServerException {
		return RequestService.getInstance().getEvent(eventId);
	}

	/*
	 * Erfragt alle vom User erstellten Events vom Server
	 * @param user User, dessen Events erfragt werden sollen
	 */
	public static List<Event> findCreatedEvents() throws NoConnectionToServerException {
		return RequestService.getInstance().getCreatedEvents(activeUser);
	}
	
	/*
	 * Erfragt alle vom User erstellten Events vom Server
	 * @param user User, dessen Events erfragt werden sollen
	 */
	public static List<Event> findUpcomingEvents() throws NoConnectionToServerException {
		return RequestService.getInstance().getUpcomingEvents(activeUser);
	}
	
	public static List<Event> findAllEvents() throws NoConnectionToServerException{
		List<Event> result = new ArrayList<Event>();
		result.addAll(findCreatedEvents());
		result.addAll(findUpcomingEvents());
		return result;
	}
	
	/*
	 * Sendet Anfrage, um das Event im Server zu löschen
	 * @param e zu löschendes Event
	 */
	public static boolean deleteEvent(Event e) throws NoConnectionToServerException {
		return RequestService.getInstance().deleteEvent(e);		
	}
	
	//Notification-Requests--------------------------------------------------------
	
	/*
	 * Lädt die Notification mit der id
	 * @param notificationId Id aus der Datenbank
	 */
	public static Notification loadNotification(long notificationId) throws UnknownObjectException, NoConnectionToServerException {
		return RequestService.getInstance().getNotification(notificationId);
	}
	
	/*
	 * Erfragt die letzten i Notifications vom Server
	 * @param i Anzahl der Benachrichtigungen
	 */
	public static List<Notification> findLastNotifications(int i) throws NoConnectionToServerException {
		return RequestService.getInstance().getLastNotifications(activeUser, i);
	}
	
	public static Notification setNotificationSeen(Notification notification) throws NoConnectionToServerException {
		return RequestService.getInstance().putNotification(notification);
	}
	
	//Einladungsmethoden-------------------------------------------------------------
	
	/*
	 * Lädt den Benutzer zum Termin ein
	 * @param user Einzuladener Benutzer
	 * @param event Termin
	 */
	public static boolean inviteToEvent(List<User> user, Event event) throws NoConnectionToServerException {
		return InvitationService.inviteToEvent(activeUser, user, event);
	}
	
	public static boolean inviteToEvent(User user, Event event) throws NoConnectionToServerException {
		return InvitationService.inviteToEvent(activeUser, user, event);
	}
	
	/*
	 * Akzeptiert als eingeloggter Benutzer die Einladung
	 * @param invitation Einladung
	 */
	public static boolean acceptInvitation(Notification invitation) throws NoConnectionToServerException {
		return InvitationService.acceptInvitation(activeUser, invitation);
	}
	
	/*
	 * Lehnt als eingeloggter Benutzer die Einladung ab
	 * @param invitation Einladung
	 */
	public static boolean declineInvitation(Notification invitation) throws NoConnectionToServerException {
		return InvitationService.declineInvitation(activeUser, invitation);
	}
	
	/*
	 * Fügt die User dem Event hinzu, ohne dass diese ablehnen können
	 * @param attendands User, die hinzugefügt werden sollen
	 * @param Event betroffenes Event
	 */
	public static Event forceUserToAttendYourEvent(List<User> attendands, Event event) throws NoConnectionToServerException {
		event.addParticipants(attendands);
		return RequestService.getInstance().putEvent(event);
	}
	
	/*
	 * Fügt die User dem Event hinzu, ohne dass diese ablehnen können
	 * @param attendands User, die hinzugefügt werden sollen
	 * @param Event betroffenes Event
	 */
	public static Event forceUserToAttendYourEvent(User user, Event event) throws NoConnectionToServerException {
		List<User> list = new ArrayList<User>();
		list.add(user);
		return forceUserToAttendYourEvent(list, event);
	}
}