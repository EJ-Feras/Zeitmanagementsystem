package de.htwsaar.pib.zms.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.repository.EventRepository;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepo;

	@Autowired
	private UserService userService;

	public Event getEventById(long id) {
		Optional<Event> event = eventRepo.findById(id);
		if (event.isPresent())
			return event.get();

		else
			return null;

	}

	public List<Event> getAllEvents() {

		return eventRepo.findAll();
	}

	/**
	 * Gibt dir eine Liste der anstehenden Events eines Users zurück, die noch
	 * anstehen und der User nicht selbst erstellt hat. (aus der Datenbank)
	 * 
	 * @param username = User dessen anstehenden Events man bekommen will
	 * @return List<User> Liste der anstehenden Events eines Users
	 */
	public List<Event> getUpcomingEvents(String username) {
		return userService.getUserByUsername(username).getUpcomingEvents();
	}

	/**
	 * Zählt wieviele Events sich in der Datenbank befinden
	 * 
	 * @return Anzahl an Events in der Datenbank (aus der Datenbank) als long
	 */
	public long count() {
		return eventRepo.count();
	}

	/**
	 * Speichert ein neues Event in die datenbank
	 * 
	 * @param newEvent
	 * @return
	 */
	public Event save(Event newEvent) {
		return eventRepo.save(newEvent);

	}

	public void deleteById(long id) {
		eventRepo.deleteById(id);
	}

	/**
	 * Verändert Attribute eines vorhandenEvents in der Datenbank
	 * 
	 * @param event Event welches verändert werden soll
	 * @return das geänderte Event, null: wenn das Event nicht existiert
	 */
	public Event changeEvent(Event event) {
		if (!isValidExistingEvent(event))
			return null;
		else {
			Event repoEvent = eventRepo.findById(event.getId()).get();

			ServerSentEventsService.notifyAboutChangeIn(repoEvent);

			repoEvent.setDate(event.getDate());
			repoEvent.setDuration(event.getDuration());
			repoEvent.setTitle(event.getTitle());
			repoEvent.setNote(event.getNote());
			repoEvent.setReminderDates(event.getReminderDates());

			if (true) { // Prüfen ob admin (und Eventersteller?)
				repoEvent.setParticipants(event.getParticipants());
				for (User u : repoEvent.getParticipants()) {
					addUserToEvent(u, repoEvent);
				}
			}
			return eventRepo.save(repoEvent);
		}
	}

	/**
	 * Entfernt einen Nutzer aus der invitedUser-List(enthält alle Nutzer die zu dem
	 * Event eingeladen wurden) von einem Event aus der Datenbank
	 * 
	 * @param sender = Nutzer, der entfernt wird
	 * @param event  = event aus dem der nutzer entfernt wird
	 * @return true, falls funktioniert .
	 */
	public boolean removeInvitedUser(User sender, Event event) {
		try {
			boolean result;
			Event e = eventRepo.findById(event.getId()).get();
			result = e.removeInvitedUser(sender);
			eventRepo.save(e);
			return result;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Fügt einen Nutzer in die invitedUser-List(enthält alle Nutzer die zu dem
	 * Event eingeladen wurden) von einem Event hinzu(in die Datenbank)
	 * 
	 * @param sender = Nutzer, der hinzugefügt wird
	 * @param event  = event zu dem der nutzer hinzugefügt wird
	 * @return true, falls funktioniert .
	 */
	public boolean addInvitedUser(User user, Event event) {
		try {
			Event e = eventRepo.findById(event.getId()).get();
//			result = e.addInvitedUser(user);
			user.addToEventsUserWasInvitedTo(e);
			userService.changeUser(user);
//			eventRepo.save(e);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Fügt mehrere Nutzer in die invitedUser-List(enthält alle Nutzer die zu dem
	 * Event eingeladen wurden) von einem Event hinzu(in die Datenbank)
	 * 
	 * @param users = Nutzer,die hinzugefügt werden
	 * @param event = event zu dem die Nutzer hinzugefügt werden
	 * @return true, falls funktioniert .
	 */
	public boolean addInvitedUsers(List<User> users, Event event) {
		boolean result = true;
		for (User u : users) {
			result = result & addInvitedUser(u, event);
		}
		return result;
	}

	/**
	 * Függt einen User als Teilnehmer zu einem Event hinzu (in die Datenbank)
	 * 
	 * @param user  = user, der hinzugefügt wird
	 * @param event = event, zu dem der user hinzugefügt wird
	 * @return true,falls funktioniert
	 */
	public boolean addUserToEvent(User user, Event event) {
		try {
			boolean result;
			Event e = eventRepo.findById(event.getId()).get();
			result = e.addParticipant(user);
			eventRepo.save(e);
			userService.changeUser(user);
			ServerSentEventsService.notifyAboutBeeingAdded(event, user);
			return result;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * gibt dir alle erstellen events eines nutzers zurück (aus die Datenbank)
	 * 
	 * @param username = username, dessen events man holt
	 * @return Liste aller erstellten Events des users
	 */
	public List<Event> findCreatedEvents(String username) {

		return userService.getUserByUsername(username).getCreatedEvents();
	}

	/**
	 * testet ob ein Event nach den gegebenen Kriterien existiert
	 * 
	 * @param e = event welches geprüft wird @returnboolean, ob das eventobjekt die
	 *          Kriterien erfüllt
	 */
	public boolean isValidExistingEvent(Event e) {
		if (e.getId() == 0) {
			return false;
		}
		if (!eventRepo.existsById(e.getId())) {
			return false;
		}
		for (User u : e.getParticipants()) {
			if (!userService.isValidExistingUser(u)) {
				return false;
			}
		}

		return true;
	}

}