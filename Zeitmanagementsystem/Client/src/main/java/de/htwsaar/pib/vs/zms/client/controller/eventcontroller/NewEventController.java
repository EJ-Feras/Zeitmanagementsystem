package de.htwsaar.pib.vs.zms.client.controller.eventcontroller;

import java.io.IOException;
import java.net.URL;
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
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class NewEventController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private DialogPane newEventRoot;
	@FXML
	private JFXTextField search, titleField, duration;
	@FXML
	private StackPane closeIconContainer;
	@FXML
	private JFXButton closeButton, saveBtn;
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

	@Setter
	private HomeController homeController;
	@Getter
	@Setter
	private static User user;
	private List<User> availableUsers;
	@Setter
	private static List<Event> createdEvents;

	/****************************** Methoden ******************************/

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setStyle();
		setIcons();
		initSaveButtonClickability();
		initSearchUserProperty();
		initializeRepetitiontypesComboBox();
		initializeAvailableUsers();
		initializeUsers();
		// RequiredFieldValidator validator = new RequiredFieldValidator();
		// validator.setMessage("Input Required");
		// titleField.getValidators().add(validator);
		// titleField.focusedProperty().addListener((o, oldVal, newVal) -> {
		// if (!newVal)
		// titleField.validate();
		// });

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void showValidationMessage(Stage owner, Control control, String tooltipText) {

	}

	/**
	 * Termin ablegen.
	 *
	 *
	 * @param event
	 *
	 * @author Feras Ejneid
	 */
	@FXML
	void saveEvent(ActionEvent event) {
		if (areImportantFieldsValid())
			return;

		Event newEvent = createEvent();
		List<User> usersTobeInvited = new ArrayList<User>();
		for (Node node : availableUsersContainer.getChildren()) {
			AnchorPane entryContainer = (AnchorPane) node;
			JFXCheckBox checkBox = ((JFXCheckBox) entryContainer.getChildren().get(2));
			if (checkBox.isSelected()) {
				Label usernameLabel = (Label) (((HBox) entryContainer.getChildren().get(0)).getChildren().get(0));
				String username = usernameLabel.getText();
				Optional<User> user = findUserByUsername(username);
				if (user.isPresent()) {
					usersTobeInvited.add(user.get());
				}
			}

		}

		user.addToCreatedEvents(newEvent);
		try {
			 newEvent = ServiceFacade.createNewEvent(newEvent);
			
			// ServiceFacade.saveUpdatedUser(user);
			ServiceFacade.inviteToEvent(usersTobeInvited, newEvent);
			// createdEvents.add(newEvent);
			homeController.initializeAllEvents();
			homeController.initializeCalendar();
			homeController.initializeTodaysEvents();
		} catch (NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		clearFields();
		WindowManager.exit(closeButton);
	}

	private Optional<User> findUserByUsername(String username) {
		return availableUsers.stream().filter(user -> user.getUsername().equals(username)).findFirst();
	}

	/**
	 * Alle Felder zuriecksetzen.
	 */
	private void clearFields() {
		titleField.clear();
		notes.clear();
		duration.clear();
		datePicker.setValue(null);
		startTime.setValue(null);
		choosedRemindersComboBox.setValue(null);
		repetitionComboBox.setValue(null);
	}

	private void initializeUsers() {
		availableUsers.stream().forEach(user -> addUserEntryInContainer(user.getUsername()));
	}

	private void addUserEntryInContainer(String userDetails) {
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			InvitableUserEntryController.setDetails(userDetails);
			Node invitableUserEntry = fxmlLoader
					.load(getClass().getResource(Constants.INVITABLE_USER_ENTRY_FXML).openStream());
			InvitableUserEntryController controller = fxmlLoader.getController();
			availableUsersContainer.getChildren().add(invitableUserEntry);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private boolean areImportantFieldsValid() {
		return titleField.getText().isEmpty() || startTime.getValue() == null || duration.getText().isEmpty();
	}

	/**
	 * Erstellt einen neuen Termin.
	 *
	 *
	 * @return
	 *
	 * @author Feras Ejneid
	 */
	private Event createEvent() {
		String title = titleField.getText().trim();
		String note = notes.getText().trim();
		Date date = createDate();
		String durationText = duration.getText().trim();
		int duration = Integer.valueOf(durationText);
		List<Date> reminders = choosedRemindersComboBox.getItems();
		Repetitiontype repetitiontype = Repetitiontype.NEVER;
		if (repetitionComboBox.getValue() != null)
			repetitiontype = repetitionComboBox.getValue();
		Event event = new Event(title, note, date, duration, reminders, repetitiontype, user);
		return event;
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

	private void initSaveButtonClickability() {
		saveBtn.disableProperty()
				.bind(Bindings.isEmpty(titleField.textProperty()).or(Bindings.isNull(datePicker.valueProperty()))
						.or(Bindings.isNull(startTime.valueProperty())).or(Bindings.isEmpty(duration.textProperty()))

		);
	}

	private void initializeRepetitiontypesComboBox() {
		repetitionComboBox.setItems(FXCollections.observableArrayList(Repetitiontype.values()));
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
		newEventRoot.getStylesheets().add(getClass().getResource(Constants.STYLES_CSS).toExternalForm());
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