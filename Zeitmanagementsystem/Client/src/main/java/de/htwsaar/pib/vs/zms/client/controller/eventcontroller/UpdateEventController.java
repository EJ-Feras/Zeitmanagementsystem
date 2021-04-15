package de.htwsaar.pib.vs.zms.client.controller.eventcontroller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.homecontroller.CalendarController;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.HomeController;
import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.utils.WindowManager;
import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.Repetitiontype;
import de.htwsaar.pib.zms.server.model.User;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Setter;

public class UpdateEventController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private DialogPane updateEventRoot;
	@FXML
	private JFXTextField search, titleField, duration;
	@FXML
	private StackPane closeIconContainer;
	@FXML
	private JFXButton closeButton, saveBtn, saveReminderBtn;
	@FXML
	private JFXDatePicker datePicker, reminderDate;
	@FXML
	private JFXTimePicker startTime, endTime, reminderTime;
	@FXML
	private JFXTextArea notes;
	@FXML
	private JFXComboBox<Repetitiontype> repetitionComboBox;
	@FXML
	private JFXComboBox<Date> choosedRemindersComboBox;
	@FXML
	private JFXCheckBox selectAllUserCheckBox;
	@FXML
	private VBox availableUsersContainer;
	@FXML
	private AnchorPane saveBtnContainer;

	@Setter
	private static Event event;
	private List<Event> events;
	private List<User> availableUsers;
	@Setter
	private TableView<Event> eventsTable;
	@Setter
	private HomeController homeController;
	@Setter
	private static AllEventsController allEventsController;
	@Setter
	private static User user;

	/****************************** Methoden ******************************/

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeAvailableUsers();
		setStyle();
		setIcons();
		initSaveButtonClickability();
		initializeRepetitiontypesComboBox();
		initializeInvitedUsers();
		initializeFields();
		initSearchUserProperty();

	}

	private void initializeInvitedUsers() {
		// invitedUsers = event.getInvitedUsers();
		// ServiceFacade.findAllUsersBelongTo(event.getId());
	}

	private void initializeAvailableUsers() {
		// if (user.getRole.equals("ROLE_SUPER_USER"))
		// availableUsers = ServiceFacade.findAllUsers();
		// else if (user.getRole.equals("ROLE_ADMIN"))
		// // Benutzer, die zu diesem Admin gehoeren
		// availableUsers = ServiceFacade.findAllUsersBelongTo(user);
		// else if (user.getRole.equals("ROLE_USER"))
		// // Benutzer, die der gleiche Admin haben
		// availableUsers = ServiceFacade.findAllUsersInSameGroup(user);

		try {
			availableUsers = ServiceFacade.getInvitableUsers();
		} catch (NoConnectionToServerException e) {
			e.printStackTrace();
		}

	}

	private void initializeFields() {

		if (event.getEventCreator().getId() != user.getId()) {
			saveBtnContainer.setVisible(false);
			saveReminderBtn.setVisible(false);
		}

		titleField.setText(event.getTitle());
		datePicker.setValue(convertToLocalDateViaInstant(event.getDate()));
		startTime.setValue(convertToLocalTime(event.getDate()));
		duration.setText(String.valueOf(event.getDuration()));
		notes.setText(event.getNote());
		repetitionComboBox.setValue(event.getRepetition());
		choosedRemindersComboBox.setItems(FXCollections.observableList(event.getReminderDates()));

		availableUsers.stream().forEach(user -> {
			boolean invited = event.getInvitedUsers().contains(user);
			boolean participant = event.getParticipants().contains(user);
			boolean inviter = event.getEventCreator().equals(user);
			String userStatus = "";
			if (invited)
				userStatus = "Eingeladen";
			else if (participant)
				userStatus = "Teilnehmer";
			else if (inviter)
				userStatus = "Einlader";
			addUserEntryInContainer(user.getUsername(), userStatus);

		});

	}

	private void initSaveButtonClickability() {
		saveBtn.disableProperty()
				.bind(Bindings.isEmpty(titleField.textProperty()).or(Bindings.isNull(datePicker.valueProperty()))
						.or(Bindings.isNull(startTime.valueProperty())).or(Bindings.isEmpty(duration.textProperty()))

		);
	}

	private LocalDate convertToLocalDateViaInstant(java.util.Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private LocalTime convertToLocalTime(java.util.Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();

	}

	private void addUserEntryInContainer(String userDetails, String userStatus) {
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			InvitableUserEntryController.setDetails(userDetails);
			InvitableUserEntryController.setStatus(userStatus);
			Node invitableUserEntry = fxmlLoader
					.load(getClass().getResource(Constants.INVITABLE_USER_ENTRY_FXML).openStream());
			InvitableUserEntryController controller = fxmlLoader.getController();
			if ("Eingeladen".equals(userStatus) || "Einlader".equals(userStatus))
				controller.deleteCheckBox();
			if ("Teilnehmer".equals(userStatus))
				controller.setCheckBox();
			availableUsersContainer.getChildren().add(invitableUserEntry);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@FXML
	void saveEvent(ActionEvent event) {
		updateEvent();
	}

	/**
	 * Erstellt einen neuen Termin.
	 *
	 *
	 * @return
	 *
	 * @author Feras Ejneid
	 */
	private void updateEvent() {
		String title = titleField.getText().trim();
		String note = notes.getText().trim();
		Date date = createDate();
		String durationText = duration.getText().trim();
		int duration = Integer.valueOf(durationText);
		List<Date> reminders = choosedRemindersComboBox.getItems();
		Repetitiontype repetitiontype = repetitionComboBox.getValue();

		event.setTitle(title);
		event.setNote(note);
		event.setDate(date);
		event.setDuration(duration);
		List<User> usersTobeInvited = new ArrayList<User>();
		System.out.println(availableUsers);
		for (Node node : availableUsersContainer.getChildren()) {
			AnchorPane entryContainer = (AnchorPane) node;
			if (entryContainer.getChildren().size() == 3) {
				JFXCheckBox checkBox = ((JFXCheckBox) entryContainer.getChildren().get(2));

				if (checkBox.isSelected() && !checkBox.isDisabled()) {
					Label usernameLabel = (Label) (((HBox) entryContainer.getChildren().get(0)).getChildren().get(0));
					
					String username = usernameLabel.getText();
					System.out.println();
					System.out.println("username -----------> " + username);
					System.out.println();
					Optional<User> user = findUserByUsername(username.trim());
					
					if (user.isPresent()) {
						System.out.println("User was found !!");
						usersTobeInvited.add(user.get());
						event.addInvitedUser(user.get());
					}
				}
			}

		}

		try {

			System.out.println("Users to invite -----------> " + usersTobeInvited);

			ServiceFacade.inviteToEvent(usersTobeInvited, event);

			events = ServiceFacade.findCreatedEvents();

			AllEventsController.setAllEvents(events);
			allEventsController.refreshEventsTable();
			homeController.initializeAllEvents();
			homeController.initializeCalendar();
			homeController.initializeTodaysEvents();

		} catch (NoConnectionToServerException e) {
			e.printStackTrace();
		}

		WindowManager.exit(closeButton);
	}

	private Optional<User> findUserByUsername(String username) {
		return availableUsers.stream().filter(user -> user.getUsername().equals(username)).findFirst();
	}

	/**
	 * Erstellt ein Datum.
	 *
	 *
	 * @return Date
	 *
	 * @author Feras Ejneid
	 */
	private Date createDate() {
		Calendar calender = Calendar.getInstance();
		calender.set(Calendar.YEAR, datePicker.getValue().getYear());
		calender.set(Calendar.MONTH, datePicker.getValue().getMonthValue() - 1);
		calender.set(Calendar.DAY_OF_MONTH, datePicker.getValue().getDayOfMonth());
		calender.set(Calendar.HOUR_OF_DAY, startTime.getValue().getHour());
		calender.set(Calendar.MINUTE, startTime.getValue().getMinute());
		calender.set(Calendar.SECOND, 0);
		calender.set(Calendar.MILLISECOND, 0);
		Date date = calender.getTime();

		return date;
	}

	@FXML
	void saveReminder(ActionEvent event) {

	}

	private void initializeRepetitiontypesComboBox() {
		repetitionComboBox.setItems(FXCollections.observableArrayList(Repetitiontype.values()));
	}

	/**
	 * Nutzerfilterneigenschaft hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void initSearchUserProperty() {
		search.textProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(newValue.trim());
		});
	}

	@FXML
	void close(ActionEvent event) {
		WindowManager.exit(closeButton);
	}

	/**
	 * den Stil der Komponenten initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void setStyle() {
		updateEventRoot.getStylesheets().add(getClass().getResource(Constants.STYLES_CSS).toExternalForm());
	}

	/**
	 * Symbole hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void setIcons() {
		MaterialIconView closeIconContainer = new MaterialIconView(MaterialIcon.CLOSE);
		closeIconContainer.setSize(Constants.SIZE_2EM);
		closeButton.setGraphic(closeIconContainer);
	}
}