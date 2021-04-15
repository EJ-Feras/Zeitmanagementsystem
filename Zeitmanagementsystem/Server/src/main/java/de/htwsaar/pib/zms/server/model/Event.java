package de.htwsaar.pib.zms.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "event")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String title;
	private String note;
	private Date date;
	private int duration;
	@ElementCollection
	@CollectionTable(name = "ReminderDates", joinColumns = @JoinColumn(name = "event_id"))
	private List<Date> ReminderDates;
	@Enumerated(EnumType.STRING)
	private Repetitiontype repetition;

	@ManyToOne()
	@JoinColumn(name = "creator_id")
	private User eventCreator;

	@ManyToMany()
	@JoinTable(name = "event_invitees", joinColumns = { @JoinColumn(name = "invitee_id") }, inverseJoinColumns = {
			@JoinColumn(name = "event_id") })
	private List<User> invitedUsers;

	@ManyToMany(mappedBy = "upcomingEvents")
	private List<User> participants = new ArrayList<User>();

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Notification> notifications;

	public Event(String title, String note, Date date, int duration, List<Date> reminderDates,
			Repetitiontype repetition, User eventCreator) {
		this.title = title;
		this.note = note;
		this.date = date;
		this.duration = duration;
		this.repetition = repetition;
		this.eventCreator = eventCreator;
		this.ReminderDates = reminderDates;
	}

	public Event(String title, String note, Date date, int duration, Repetitiontype repetition, User eventCreator) {
		this.title = title;
		this.note = note;
		this.date = date;
		this.duration = duration;
		this.repetition = repetition;
		this.eventCreator = eventCreator;

	}

	public Event() {

	}

	/*
	 * Fügt einen Benutzer zu den eingeladenen Nutzern hinzu, wenn dieser noch nicht
	 * in der Liste ist
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public boolean addInvitedUser(User person) {
		if (!invitedUsers.contains(person)) {
			return invitedUsers.add(person);
		}
		return false;
	}

	/*
	 * Entfernt einen Benutzer aus den eingeladenen Nutzern, wenn dieser in der
	 * Liste ist
	 * 
	 * @return Boolean, ob erfolgreich
	 */
	public boolean removeInvitedUser(User person) {
		if (invitedUsers.contains(person)) {
			return invitedUsers.remove(person);
		}
		return false;
	}

	/**
	 * Methode um mehrere Nutzer ( List<User> ) einzuladen.(Nur Nutzer die noch
	 * nicht eingeladet sind)
	 * 
	 * @param invited = User, der zum Event eingeladen sind
	 */
	public void addInvitedUsers(List<User> invited) {
		for (User u : invited) {
			addInvitedUser(u);
		}
	}

	public void addParticipants(List<User> attendands) {
		for (User u : attendands) {
			addParticipant(u);
		}
	}

	/**
	 * @param sender = Teilnehmer, der zum Event hinzugefügt wird
	 * @return nur true, wenn Nutzer noch nicht eingeladen und existent.
	 */
	public boolean addParticipant(User sender) {
		if (!participants.contains(sender)) {
			participants.add(sender);
			sender.getUpcomingEvents().add(this);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return this.title + ": " + this.note;
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
		Event other = (Event) obj;
		if (id != other.id)
			return false;
		return true;
	}

}