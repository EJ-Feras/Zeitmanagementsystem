package de.htwsaar.pib.vs.zms.client.controller.DeleteController;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Steuert den Löschbestätigungsdialog.
 * 
 * @author Feras
 *
 */
public class DeletionController implements Initializable {

	@FXML
	DialogPane dialogPane;

	@FXML
	Label msgText;

	@FXML
	JFXButton yes;

	@FXML
	JFXButton no;

	private ObservableList<User> users;
	private ObservableList<Event> events;
	private ObservableList<Notification> notifications;

	private User user;
	private Event event;

	private boolean deleteUser;
	private boolean deleteEvent;
	private boolean deleteNotification;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setStyle();
	}

	public void setUsersList(ObservableList<User> users) {
		this.users = users;

		deleteUser = true;
		deleteEvent = false;
		deleteNotification = false;

		if (users.size() == 1)
			msgText.setText("  Wollen Sie diesen Benutzer löschen ?");
		else
			msgText.setText("  Wollen Sie diese Benutzer löschen ?");
	}

	public void setUser(User user) {
		this.user = user;

		deleteUser = true;
		deleteEvent = false;
		deleteNotification = false;

		msgText.setText("  Wollen Sie diesen Benutzer löschen ?");

	}

	public void setEventsList(ObservableList<Event> events) {
		this.events = events;

		deleteEvent = true;
		deleteUser = false;
		deleteNotification = false;

		if (events.size() == 1)
			msgText.setText("  Wollen Sie diesen Termin löschen ?");
		else
			msgText.setText("  Wollen Sie diese Termine löschen ?");
	}

	public void setEvent(Event event) {
		this.event = event;

		deleteEvent = true;
		deleteUser = false;
		deleteNotification = false;

		msgText.setText("  Wollen Sie diesen Termin löschen ?");

	}

	public void setNotificationsList(ObservableList<Notification> notifications) {
		this.notifications = notifications;

		deleteNotification = true;
		deleteEvent = true;
		deleteUser = false;

		if (notifications.size() == 1)
			msgText.setText("  Wollen Sie diese Benachrichtigung löschen ?");
		else
			msgText.setText("  Wollen Sie diese Benachrichtigungen löschen ?");
	}

	@FXML
	void delete(ActionEvent event) {
		delete();

		final Node source = (Node) event.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();

	}

	@FXML
	void cancel(ActionEvent event) {
		final Node source = (Node) event.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();

	}

	private void delete() {

		if (deleteUser)
			try {
				System.out.println("User To be deleted ---> " + user.getUsername());
				ServiceFacade.deleteUser(user);
			} catch (NoConnectionToServerException e) {
				e.printStackTrace();
			}

		else if (deleteEvent)
			try {
				ServiceFacade.deleteEvent(event);
			} catch (NoConnectionToServerException e) {
				e.printStackTrace();
			}

		// else if (deleteNotification)
		// notifications.stream().forEach(n ->
		// ServiceFacade.deleteNotification(n));

	}

	private void setStyle() {
		dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
	}


}
