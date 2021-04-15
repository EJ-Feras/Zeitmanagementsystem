package de.htwsaar.pib.vs.zms.client.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;

import de.htwsaar.pib.vs.zms.client.serviceMessages.*;
import de.htwsaar.pib.zms.server.service.EventMessages;
import reactor.core.publisher.Flux;

public class GuiMessenger {
	
	private static ZmsObserver observer;
	private static Flux<ServerSentEvent<String>> eventStream;
	
	/*
	 * Wertet ServerSentEvent aus und informiert die PräsentationsSchicht über ein ZmsEvent
	 * @param content ServerSentEvent
	 */
	public static void handleEvent (ServerSentEvent<String> content) { 
		
		System.out.println(content);
		if (observer != null) {
			ZmsEvent event = null;
			String message = content.data();
			String[] parts = message.split(":");
			String eventMessage = parts[0];
			
				if  (EventMessages.CHANGE_IN_EVENT.equals(eventMessage)) { 
					event = new ZmsEvent(Long.parseLong(parts[1]), Change.update, Modelclass.Event);
				}
				
				else if  (EventMessages.NEW_EVENT.equals(eventMessage)) { 
					event = new ZmsEvent(Long.parseLong(parts[1]), Change.newObject, Modelclass.Event);
				}
				
				else if  (EventMessages.DELETED_EVENT.equals(eventMessage)) { 
					event = new ZmsEvent(Long.parseLong(parts[1]), Change.deletedObject, Modelclass.Event);
				}
				
				else if  (EventMessages.NEW_NOTIFICATION.equals(eventMessage)) { 
					event = new ZmsEvent(Long.parseLong(parts[1]), Change.newObject, Modelclass.Notification);
				}
				
				else if  (EventMessages.DELETED_NOTIFICATION.equals(eventMessage)) { 
					event = new ZmsEvent(Long.parseLong(parts[1]), Change.deletedObject, Modelclass.Notification);
				}
				
				else if  (EventMessages.CHANGE_IN_EVENT.equals(eventMessage)) { 
					event = new ZmsEvent(Long.parseLong(parts[1]), Change.update, Modelclass.Event);
				}

			
			observer.handleChangeInObjects(event);
		}
	}
	
	/*
	 * Informiert die GUI, dass die Verbindung zum Server wiederhergestellt wurde
	 */
	public static void informAboutRestoredConnection() {
		if (observer != null) {
			observer.handleReconnectionToServer();
		}
	}
	
	
	/*
	 * Sendet Get-Request für einen SseEmitter, um Events vom Server zu empfangen
	 * @param username Benutzername
	 * @param password Passwort
	 * @return SseEmitter
	 */
	public static boolean subscribeToServer() {

		 ParameterizedTypeReference<ServerSentEvent<String>> type 
		 = new ParameterizedTypeReference<ServerSentEvent<String>>() {};
	     
		 eventStream = RequestService.getInstance().subscribe();

		 if (eventStream != null) {
	    	    eventStream.subscribe(
	    	      content -> {GuiMessenger.handleEvent(content);},
	    	      error -> {GuiMessenger.handleEventError(error);});
	    	    return true;
		 }
		 else {
			 return false;
		 }
	}
	
	/*
	 * Geht mit Fehler beim ServerSentEvent um
	 */
	public static void handleEventError(Throwable error) {
		
	}
	
	/*
	 * Lässt GUI für Server-Sent-Events und Verbindungsabbrüche abonnieren
	 */
	public static boolean subscribe(ZmsObserver o) {
		observer = o;
		return true;
	}
}