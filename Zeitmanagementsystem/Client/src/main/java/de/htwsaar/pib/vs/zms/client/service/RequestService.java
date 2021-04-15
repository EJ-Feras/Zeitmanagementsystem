package de.htwsaar.pib.vs.zms.client.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import de.htwsaar.pib.vs.zms.client.exceptions.*;
import de.htwsaar.pib.zms.server.auth.PasswordGenerator;
import de.htwsaar.pib.zms.server.model.*;

public class RequestService {

	private static final int port = 8082;

	private static RequestService instance;

	private WebClient webClient;

	private String userName;
	private String password;

	/*
	 * Konstruktor
	 * 
	 * @param baseURI Basis-URI für anfragen
	 */
	private RequestService(String baseURI) {
		webClient = WebClient.create(baseURI);
	}

	public static RequestService getInstance() {
		return getInstance("http://localhost:" + port);
	}

	public static RequestService getInstance(String baseURI) {
		if (instance == null) {
			instance = new RequestService(baseURI);
		}
		return instance;
	} 

	// Methoden für User
	// --------------------------------------------------------------------

	/*
	 * Sendet Get-Request für einen User
	 * 
	 * @param username Benutzername
	 * 
	 * @param password Passwort
	 * 
	 * @return Benutzer
	 */
	public User getUser(String username, String password)
			throws UnknownObjectException, WrongUserDetailsException, NoConnectionToServerException {
		this.userName = username;
		this.password = password; // PasswordGenerator.generatePW(password);
		return sendGetRequestSingle("/user/" + username, User.class);

	}

	/*
	 * Sendet Get-Request für alle Personen
	 * 
	 * @return Liste aller Personen
	 */
	public List<User> getAllUsers() throws NoConnectionToServerException {
		return sendGetRequestMultiple("/users", User.class);
	}

	/*
	 * Lädt den aktuell eingeloggten User erneut
	 * 
	 * @return User Objekt
	 */
	public User refreshCurrentUser() throws UnknownObjectException, NoConnectionToServerException {
		try {
			return sendGetRequestSingle("/user/" + userName, User.class);
		} catch (WrongUserDetailsException e) {
			//Sollte nicht passieren
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * Sendet Put-Request für einen User
	 * 
	 * @param user Benutzer
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public User putUser(UserPasswordStruct user) throws NoConnectionToServerException {
		return sendPutRequest("/user", UserPasswordStruct.class, user).user;
	}

	/*
	 * Sendet Post-Request für einen User
	 * 
	 * @param User Benutzer
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public User postUser(User user, String password) throws NoConnectionToServerException {
		UserPasswordStruct response = sendPostRequest("/user", UserPasswordStruct.class, new UserPasswordStruct(user, password));
		if (response != null) {
			return response.user;
		}
		return null;
	}

	/*
	 * Sendet Delete-Request für einen User
	 * 
	 * @param user Benutzer
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public boolean deleteUser(User user) throws NoConnectionToServerException {
		return sendDeleteRequest("/user/" + user.getUsername());
	}

	// Methoden für EventEmitter
	// -----------------------------------------------------------

	/*
	 * Sendet Get-Request für einen SseEmitter, um Events vom Server zu
	 * empfangen
	 * 
	 * @param username Benutzername
	 * 
	 * @param password Passwort
	 * 
	 * @return SseEmitter
	 */
	public Flux<ServerSentEvent<String>> subscribe() {

		ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<ServerSentEvent<String>>() {
		};

		Flux<ServerSentEvent<String>> eventStream = sendGetRequest("/SseEmitter/" + userName).bodyToFlux(type);

		return eventStream;
	}

	/*
	 * Sendet Put-Request für einen SseEmitter, um sich abzumelden
	 * 
	 * @param username Benutzername
	 * 
	 * @param password Passwort
	 * 
	 * @return SseEmitter
	 */
	public boolean unsubscribe(String username, String password, SseEmitter e) throws NoConnectionToServerException {
		sendPutRequest("/SseEmitter/" + username, SseEmitter.class, e);
		return true;
	}

	// Methoden für Event
	// --------------------------------------------------------------------------------

	/*
	 * Sendet Get-Request einen Termin
	 * 
	 * @param User Benutzer
	 * 
	 * @return Termin
	 */
	public Event getEvent(long eventId) throws UnknownObjectException, NoConnectionToServerException {
		try {
			return sendGetRequestSingle("/event/" + eventId, Event.class);
		} catch (WrongUserDetailsException e) {
			// Kann nicht passieren
		}
		return null;
	}

	/*
	 * Sendet Get-Request für Termine eines Users
	 * 
	 * @param User Benutzer
	 * 
	 * @return Liste der Termine
	 */
	public List<Event> getCreatedEvents(User user) throws NoConnectionToServerException {
		return sendGetRequestMultiple("/user/" + user.getUsername() + "/event/createdEvents", Event.class);
	}

	/*
	 * Sendet Get-Request für Termine eines Users
	 * 
	 * @param User Benutzer
	 * 
	 * @return Liste der Termine
	 */
	public List<Event> getUpcomingEvents(User user) throws NoConnectionToServerException {
		return sendGetRequestMultiple("/user/" + user.getUsername() + "/event/upcomingEvents", Event.class);
	}

	/*
	 * Sendet Put-Request für Termin
	 * 
	 * @param event Termin
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public Event putEvent(Event event) throws NoConnectionToServerException {
		return sendPutRequest("/event", Event.class, event);
	}

	/*
	 * Sendet Post-Request für Termin
	 * 
	 * @param event Termin
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public Event postEvent(Event event) throws NoConnectionToServerException {
		return sendPostRequest("/event", Event.class, event);
	}

	/*
	 * Sendet Delete-Request für Termin
	 * 
	 * @param event Termin
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public boolean deleteEvent(Event event) throws NoConnectionToServerException {
		return sendDeleteRequest("/event/" + event.getId());
	}

	// Methoden für Notification
	// -------------------------------------------------------------------

	/*
	 * Sendet Get-Request einen Termin
	 * 
	 * @param User Benutzer
	 * 
	 * @return Termin
	 */
	public Notification getNotification(long notificationId)
			throws UnknownObjectException, NoConnectionToServerException {
		try {
			return sendGetRequestSingle("/notification/" + notificationId, Notification.class);
		} catch (WrongUserDetailsException e) {
			// Kann nicht passieren
		}
		return null;
	}

	/*
	 * Erfragt die letzten i Notifications vom User
	 * 
	 * @param user Benutzer
	 * 
	 * @param i Anzahl der zu ladenden Notifications
	 */
	public List<Notification> getLastNotifications(User user, int i) throws NoConnectionToServerException {
		return sendGetRequestMultiple("/notification/" + user.getId() + "/" + i, Notification.class);
	}

	/*
	 * Sendet Put-Request für Termin
	 * 
	 * @param event Termin
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public Notification postNotification(Notification notification) throws NoConnectionToServerException {
		return sendPostRequest("/notification", Notification.class, notification);
	}
	
	public Notification putNotification(Notification notification) throws NoConnectionToServerException {
		return sendPutRequest("/notification", Notification.class, notification);
	}

	/*
	 * Sendet Delete-Request für Termin
	 * 
	 * @param event Termin
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public boolean deleteNotification(Notification notification) throws NoConnectionToServerException {
		return sendDeleteRequest("/notification");
	}

	// Generische Methoden für alle Operationen
	// ----------------------------------------------------

	/*
	 * Sendet eine Get-Request für ein einzelnes Objekt
	 * 
	 * @param url URI für die Anfrage
	 * 
	 * @param c Klasse des Objekts
	 */
	private <T> T sendGetRequestSingle(String url, Class<T> c)
			throws UnknownObjectException, WrongUserDetailsException, NoConnectionToServerException {
		try {
			T object = sendGetRequest(url).bodyToMono(c).block();
			if (object != null) {
				return object;
			} else {
				throw new UnknownObjectException();
			}
		} catch (WebClientRequestException e) {
			tryReconnecting();
			throw new NoConnectionToServerException();
		}
		catch (WebClientResponseException e){
			throw new WrongUserDetailsException();
		}
	}

	/*
	 * Sendet eine Get-Request für mehrere Objekte
	 * 
	 * @param url URI für die Anfrage
	 * 
	 * @param c Klasse der Objekte
	 */
	private <T> List<T> sendGetRequestMultiple(String url, Class<T> c) throws NoConnectionToServerException {
		try {
			return sendGetRequest(url).bodyToFlux(c).collectList().block();
		} catch (WebClientRequestException e) {
			tryReconnecting();
			throw new NoConnectionToServerException();
		}
	}

	/*
	 * Sendet eine Get-Request für mehrere Objekte
	 * 
	 * @param url URI für die Anfrage
	 * 
	 * @param c Klasse der Objekte
	 */
	private <T> ResponseSpec sendGetRequest(String url) {
		return webClient.get().uri(url).headers(httpHeaders -> httpHeaders.setBasicAuth(this.userName, this.password))
				.retrieve();
	}

	/*
	 * Sendet eine Post-Request
	 * 
	 * @param url URI für die Anfrage
	 * 
	 * @param c Klasse des Objekts
	 * 
	 * @param object gesendetes Objekt
	 */
	private <T> T sendPostRequest(String url, Class<T> c, T object) throws NoConnectionToServerException {
		try {
			T response = webClient.post().uri(url)
					.headers(httpHeaders -> httpHeaders.setBasicAuth(this.userName, this.password))
					.body(Mono.just(object), c).retrieve().bodyToMono(c).block();

			return response;
		} catch (WebClientRequestException e) {
			tryReconnecting();
			throw new NoConnectionToServerException();
		}
	}

	/*
	 * Sendet eine Put-Request
	 * 
	 * @param url URI für die Anfrage
	 * 
	 * @param c Klasse des Objekts
	 * 
	 * @param object gesendetes Objekt
	 */
	private <T> T sendPutRequest(String url, Class<T> c, T object) throws NoConnectionToServerException {
		try {
			T response = webClient.put().uri(url)
					.headers(httpHeaders -> httpHeaders.setBasicAuth(this.userName, this.password))
					.body(Mono.just(object), c).retrieve().bodyToMono(c).block();

			return response;
		} catch (WebClientRequestException e) {
			tryReconnecting();
			throw new NoConnectionToServerException();
		}
	}

	/*
	 * Sendet eine Delete-Request
	 * 
	 * @param url URI für die Anfrage
	 */
	private boolean sendDeleteRequest(String url) throws NoConnectionToServerException {
		try {
			Object response = webClient.delete().uri(url)
					.headers(httpHeaders -> httpHeaders.setBasicAuth(this.userName, this.password)).retrieve()
					.bodyToMono(Void.class).block();

			return true;
		} catch (WebClientRequestException e) {
			tryReconnecting();
			throw new NoConnectionToServerException();
		}
	}

	/*
	 * Startet Thread, um die Verbindung wieder aufzubauen
	 */
	private void tryReconnecting() {
		Reconnector reconnector = Reconnector.getInstance();

		if (reconnector != null) {
			reconnector.run();
		}
	}

}
