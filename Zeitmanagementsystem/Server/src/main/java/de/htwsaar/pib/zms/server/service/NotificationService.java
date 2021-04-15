package de.htwsaar.pib.zms.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.repository.NotificationRepository;

@Service
public class NotificationService {

	@Autowired
	private UserService userService;

	@Autowired
	private EventService eventService;

	@Autowired
	private NotificationRepository notifiRepo;

	public List<Notification> getAllNotifications() {
		List<Notification> notifications = new ArrayList<>();
		notifiRepo.findAll().forEach(notifications::add);
		return notifications;
	}

	public Optional<Notification> getNotificationById(long id) {
		return notifiRepo.findById(id);
	}

	/**
	 * gib dir die Liste der (count)-letzten Notification eines Users
	 * 
	 * @param userid = userid dessen Notifications man holt
	 * @param count  = gibt an wieviele der (count)-letzten Notifications man
	 *               mitgibt
	 * @return Liste derzurückgegebenen Elemente
	 */
	public List<Notification> getLatestNotification(long userid, int count) {
		List<Notification> allNotifications = getAllNotificationsOfUser(userid);
		if (count <= allNotifications.size()) {
			return allNotifications.subList(allNotifications.size() - count, allNotifications.size());
		} else {
			return allNotifications;
		}
	}

	public List<Notification> getAllNotificationsOfUser(long userId) {
		User u = userService.getUserByid(userId);
		return u.getNotifications();

	}

	/**
	 * Fügt eine Notifcation der Datenbank hinzu und führt die dazugehörenden
	 * Befehle aus Wenn Notification = Event akzeptieren: user wird dem Event
	 * zugefügt. Wenn Notification = Event ablehnen: user wird aus dem Event
	 * entfernt Wenn Notification = Zum Event einladen: Die User werden in die
	 * invitedUser-Liste hinzugefügt Zum abschluss wird ein Befehl zum Senden einer
	 * Notification als Antwort ausgeführt
	 * 
	 * @param notification = Notification die der Datenbank hinzugefügt wird
	 * @return boolean, ob es funktioniert hat
	 */
	public boolean addNotification(Notification notification) {
		boolean result = false;
		switch (notification.getNotificationtype()) {
		case acceptance:
			result = eventService.addUserToEvent(notification.getSender(), notification.getEvent());
			break;
		case rejection:
			result = eventService.removeInvitedUser(notification.getSender(), notification.getEvent());
			break;
		case invitation:
			result = eventService.addInvitedUsers(notification.getReceivers(), notification.getEvent());
			break;
		default:
			return result;
		}

		notifiRepo.save(notification);
		// ServerSentEventsService.notifyAboutNewNotification(notification);
		return true;
	}

	public void setNotificationSeen(Notification notification) {
		Optional<Notification> old = notifiRepo.findById(notification.getId());
		if (old.isPresent()) {
			old.get().setSeen(notification.isSeen());
			notifiRepo.save(old.get());
		}

	}

	public boolean deletNotificationById(long id) {
		Optional<Notification> n = notifiRepo.findById(id);
		if (n == null)
			return false;
		if (n.isPresent()) {
			notifiRepo.deleteById(id);
			ServerSentEventsService.notifyAboutDeletedNotification(n.get());
		}
		return true;
	}

	/**
	 * testet ob eine Notification nach den gegebenen Kriterien existiert
	 * 
	 * @param Notification n = Notification welches geprüft wird
	 * @return boolean, ob das Notificationobjekt die Kriterien erfüllt
	 */
	boolean isValidExistingNotification(Notification n) {
		if (n.getId() == 0) {
			return false;
		}

		if (!notifiRepo.existsById(n.getId())) {
			return false;
		}

		if (!userService.isValidExistingUser(n.getSender())) {
			return false;
		}
		for (User u : n.getReceivers())
			if (!userService.isValidExistingUser(u)) {
				return false;
			}
		if (!eventService.isValidExistingEvent(n.getEvent())) {
			return false;
		}
		return true;
	}

}