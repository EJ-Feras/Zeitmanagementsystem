package de.htwsaar.pib.zms.server.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	// Damit bei der Übergabe des Userobjekts nicht das Passwort mit ausgehändigt
	// wird.
	@JsonIgnore
	private String password;
	private String username;
	private String firstName;
	private String secondName;
	private String email;
	private String role;
	private boolean enabled;

	@ManyToMany(mappedBy = "invitedUsers", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Event> eventsUserWasInvitedTo = new ArrayList<Event>();

	@OneToMany(mappedBy = "eventCreator", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Event> createdEvents = new ArrayList<Event>();

	@ManyToMany()
	@JoinTable(name = "participants", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "event_id") })
	@JsonIgnore
	private List<Event> upcomingEvents = new ArrayList<Event>();

	@ManyToMany(mappedBy = "receivers", cascade = CascadeType.ALL)
	@ToString.Exclude
	@JsonIgnore
	List<Notification> notifications = new ArrayList<Notification>();

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	@JsonIgnore
	List<Notification> sentNotifications = new ArrayList<Notification>();

	public User(String firstName, String secondName, String username, String role, String email) {

		if (!UserRole.roleExists(role)) {
			throw new IllegalArgumentException("Unknown role " + role);
		}

		this.username = username;
		this.firstName = firstName;
		this.secondName = secondName;
		this.email = email;
		this.role = role;

	}

	public User() {

	}

	public void inviteTo(Event event, User inviter) {

	}

	/**
	 * Fügt dem Nutzer ein event hinzu, welches er nicht selbst erstellt hat. Der
	 * Nutzer darf auch nicht an dem Event teilnehmen
	 * 
	 * @param event = event, zu welchem der Nutzer hinzugefügt wird
	 * @return true, falls erfolgreich(false kann auch bedeuten, dass das Event
	 *         schon hinzugefügt wurde)
	 */
	public boolean addupcommingEvent(Event event) {
		if (this.upcomingEvents.contains(event)) {
			this.upcomingEvents.add(event);
			event.addParticipant(this);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Fügt dem Nutzer ein von ihm erstelltes Event in die createdEvents-List hinzu
	 * 
	 * @param event welches dem Nutzer hinzugefügt wird
	 * @return true, falls erfolgreich(false kann auch bedeuten, dass das Event
	 *         schon hinzugefügt wurde)
	 */
	public boolean addToCreatedEvents(Event event) {
		if (!this.createdEvents.contains(event)) {
			return this.createdEvents.add(event);
		} else {
			return false;
		}
	}

	public boolean addToEventsUserWasInvitedTo(Event event) {
		if (this.eventsUserWasInvitedTo.contains(event)) {
			this.eventsUserWasInvitedTo.add(event);
			event.addInvitedUser(this);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Fügt dem Nutzer eine Notification hinzu
	 * 
	 * @param notification = Notification die hinzugefügt wird
	 * @return true, falls erfolgreich(false kann auch bedeuten, dass das Event
	 *         schon hinzugefügt wurde)
	 */
	public boolean addNotification(Notification notification) {
		if (!this.notifications.contains(notification)) {
			return this.notifications.add(notification);
		} else {
			return false;
		}
	}

	public String getRole() {
		return role;
	}

	@Override
	public String toString() {
		return this.firstName + " " + this.secondName + ", " + this.username;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
