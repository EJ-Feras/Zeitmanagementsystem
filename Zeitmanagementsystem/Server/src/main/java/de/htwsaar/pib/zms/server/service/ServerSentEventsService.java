package de.htwsaar.pib.zms.server.service;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import de.htwsaar.pib.zms.server.model.*;


public class ServerSentEventsService {

	private ConcurrentHashMap<String, SseEmitter> subscribers = new ConcurrentHashMap<String, SseEmitter>();

	private static ServerSentEventsService instance;

	private static ServerSentEventsService getInstance() {
		if (instance == null) {
			instance = new ServerSentEventsService();
			try {
				Thread a = new Thread(new ConnectionChecker());
				a.start();
			} catch (Exception e) {
				System.out.println("error");
			}
		}
		return instance;
	}

	public static SseEmitter subscribe(String username) {

		SseEmitter emitter = new SseEmitter();

		getInstance().subscribers.put(username, emitter);
		return emitter;
	}

	public static void unsubscribe(String username) {
		getInstance().subscribers.remove(username);
	}

	public static void sendEventTo(User user, String message) {
		sendEventTo(user.getUsername(), message);
	}

	public static void sendEventTo(String username, String message) {
		if (getInstance().subscribers.containsKey(username)) {
			SseEmitter emitter = getInstance().subscribers.get(username);

			try {
				emitter.send(message);
				System.out.println("Event gesendet");
			} catch (IOException e1) {
				getInstance().subscribers.remove(username);
			}
		}
	}

	public static void notifyAboutChangeIn(Event e) {
		for (User user : e.getParticipants()) {
			sendEventTo(user, EventMessages.CHANGE_IN_EVENT + ":" + e.getId());
		}
		sendEventTo(e.getEventCreator(), EventMessages.CHANGE_IN_EVENT + ":" + e.getId());
	}

	public static void notifyAboutDeleted(Event e) {
		for (User user : e.getParticipants()) {
			sendEventTo(user, EventMessages.DELETED_EVENT + ":" + e.getId());
		}
		sendEventTo(e.getEventCreator(), EventMessages.DELETED_EVENT + ":" + e.getId());
	}

	public static void notifyAboutNewNotification(Notification n) {
		for (User receiver : n.getReceivers())
			sendEventTo(receiver, EventMessages.NEW_NOTIFICATION + ":" + n.getId());
	}

	public static void notifyAboutChangeIn(User u) {
		// sendEventTo(u, EventMessages.CHANGE_IN_USER + ":");
	}

	public static void SendToAllUsers(String message) {

		Enumeration<String> users = getInstance().subscribers.keys();

		while (users.hasMoreElements()) {
			sendEventTo(users.nextElement(), message);
		}

	}

	public static void notifyAboutBeeingAdded(Event event, User receiver) {
		sendEventTo(receiver, EventMessages.NEW_EVENT + ":" + event.getId());
	}

	public static void notifyAboutDeletedNotification(Notification n) {
		for (User receiver : n.getReceivers())
			sendEventTo(receiver, EventMessages.DELETED_NOTIFICATION + ":" + n.getId());
	}

}
