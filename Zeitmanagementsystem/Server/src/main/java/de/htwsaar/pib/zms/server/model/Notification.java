package de.htwsaar.pib.zms.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String title;
	@Lob
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event event;
	@Enumerated(EnumType.STRING)
	private Notificationtype notificationtype;

	@ManyToMany()
	@JoinTable(name = "users_notifi", joinColumns = { @JoinColumn(name = "notifi_id") }, inverseJoinColumns = {
			@JoinColumn(name = "user_id") })
	private List<User> receivers = new ArrayList<User>();

	private Date sendDate;
	private boolean seen;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "sender_id")
	private User sender;

	/**
	 * Kreeiert eine Einladungsnotification zu einem Event
	 * 
	 * @param inviter User, der eine Einladung sendet
	 * @param event   event zu dem Eingeladen wird
	 * @return die Notification mit Text in "title" und "description" als konkrete
	 *         Nachricht
	 */
	public static Notification createInvitation(User inviter, Event event) {
		return new Notification("Einladung zu Termin",
				inviter.getUsername() + "hat dich zum Termin " + event.getTitle() + " eingeladen", event,
				Notificationtype.invitation, inviter);
	}

	/**
	 * Wenn eine Einladung geschickt worden ist, und diese angenommen wurde, wird
	 * eine Notification erstellt, die im title und description die Teilnahme an
	 * Event übermittelt
	 * 
	 * @param invited     der eingeladene User
	 * @param invitation: Nachricht des Einladers
	 * @return Notification mit Annahmenachricht im title und description
	 */
	public static Notification createAcceptance(User invited, Notification invitation) {
		return new Notification(
				invited.getUsername() + " hat deine Einladung akzeptiert", invited.getUsername()
						+ "hat deine Einladung zum Termin " + invitation.getEvent().getTitle() + " akzeptiert.",
				invitation.getEvent(), Notificationtype.acceptance, invited);
	}

	/**
	 * Wenn eine Einladung geschickt worden ist, und diese abgelehnt wurde, wird
	 * eine Notification erstellt, die im title und description die Ablehnung zum
	 * event beinhaltet.
	 * 
	 * @param invited     der eingeladene User
	 * @param invitation: Nachricht des Einladers
	 * @return Notification mit Ablehnungsnachricht im title und description
	 */
	public static Notification createRejection(User invited, Notification invitation) {
		return new Notification(
				invited.getUsername() + " hat deine Einladung abgelehnt", invited.getUsername()
						+ "hat deine Einladung zum Termin " + invitation.getEvent().getTitle() + " abgelehnt.",
				invitation.getEvent(), Notificationtype.rejection, invited);
	}

	public Notification(String title, String description, Event event, Date sendDate, Notificationtype notificationtype,
			User sender) {
		this.title = title;
		this.description = description;
		this.event = event;
		this.sendDate = sendDate;
		this.notificationtype = notificationtype;
		this.sender = sender;
	}

	public Notification() {

	}

	public Notification(String title, String description, Event event, Notificationtype notificationtype, User sender) {
		this(title, description, event, new Date(System.currentTimeMillis()), notificationtype, sender);
	}

	/**
	 * Fügt der Notification einen (weiteren) Empfänger hinzu.
	 * 
	 * @param u = user der als Empfänger hinuzgefügt wird
	 */
	public void setReceiver(User u) {
		this.receivers.add(u);
	}

	@Override
	public String toString() {
		return this.title + ": " + this.description;
	}

}
