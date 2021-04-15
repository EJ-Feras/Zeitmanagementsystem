package de.htwsaar.pib.vs.zms.client.controller.homecontroller;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.eventcontroller.AllEventsController;
import de.htwsaar.pib.vs.zms.client.controller.eventcontroller.CalendarEventCellController;
import de.htwsaar.pib.vs.zms.client.controller.eventcontroller.NewEventController;
import de.htwsaar.pib.vs.zms.client.controller.logincontroller.LoginController;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.HomeController;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.MainViewController;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.utils.DateTime;
import de.htwsaar.pib.vs.zms.client.utils.ResizeHelper;
import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.User;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.Setter;

public class CalendarController implements Initializable {

	/****************************** final ******************************/
	private static final int MAX_YEAR = 2100;

	/****************************** FX-Attribute ******************************/
	@FXML
	private AnchorPane calendarPane;
	@FXML
	private Label sunday, monday, tuesday, wednesday, thursday, friday, saturday;
	@FXML
	private AnchorPane anchorPane01, anchorPane11, anchorPane21, anchorPane31, anchorPane41, anchorPane51, anchorPane61,
			anchorPane02, anchorPane12, anchorPane22, anchorPane32, anchorPane42, anchorPane52, anchorPane62,
			anchorPane03, anchorPane13, anchorPane23, anchorPane33, anchorPane43, anchorPane53, anchorPane63,
			anchorPane04, anchorPane14, anchorPane24, anchorPane34, anchorPane44, anchorPane54, anchorPane64,
			anchorPane05, anchorPane15, anchorPane25, anchorPane35, anchorPane45, anchorPane55, anchorPane65,
			anchorPane06, anchorPane16, anchorPane26, anchorPane36, anchorPane46, anchorPane56, anchorPane66;
	@FXML
	private Label numberOfDayLabel01, numberOfDayLabel11, numberOfDayLabel21, numberOfDayLabel31, numberOfDayLabel41,
			numberOfDayLabel51, numberOfDayLabel61, numberOfDayLabel02, numberOfDayLabel12, numberOfDayLabel22,
			numberOfDayLabel32, numberOfDayLabel42, numberOfDayLabel52, numberOfDayLabel62, numberOfDayLabel03,
			numberOfDayLabel13, numberOfDayLabel23, numberOfDayLabel33, numberOfDayLabel43, numberOfDayLabel53,
			numberOfDayLabel63, numberOfDayLabel04, numberOfDayLabel14, numberOfDayLabel24, numberOfDayLabel34,
			numberOfDayLabel44, numberOfDayLabel54, numberOfDayLabel64, numberOfDayLabel05, numberOfDayLabel15,
			numberOfDayLabel25, numberOfDayLabel35, numberOfDayLabel45, numberOfDayLabel55, numberOfDayLabel65,
			numberOfDayLabel06, numberOfDayLabel16, numberOfDayLabel26, numberOfDayLabel36, numberOfDayLabel46,
			numberOfDayLabel56, numberOfDayLabel66;

	@FXML
	private VBox calendarCellEntriesContainer01, calendarCellEntriesContainer11, calendarCellEntriesContainer21,
			calendarCellEntriesContainer31, calendarCellEntriesContainer41, calendarCellEntriesContainer51,
			calendarCellEntriesContainer61, calendarCellEntriesContainer02, calendarCellEntriesContainer12,
			calendarCellEntriesContainer22, calendarCellEntriesContainer32, calendarCellEntriesContainer42,
			calendarCellEntriesContainer52, calendarCellEntriesContainer62, calendarCellEntriesContainer03,
			calendarCellEntriesContainer13, calendarCellEntriesContainer23, calendarCellEntriesContainer33,
			calendarCellEntriesContainer43, calendarCellEntriesContainer53, calendarCellEntriesContainer63,
			calendarCellEntriesContainer04, calendarCellEntriesContainer14, calendarCellEntriesContainer24,
			calendarCellEntriesContainer34, calendarCellEntriesContainer44, calendarCellEntriesContainer54,
			calendarCellEntriesContainer64, calendarCellEntriesContainer05, calendarCellEntriesContainer15,
			calendarCellEntriesContainer25, calendarCellEntriesContainer35, calendarCellEntriesContainer45,
			calendarCellEntriesContainer55, calendarCellEntriesContainer65, calendarCellEntriesContainer06,
			calendarCellEntriesContainer16, calendarCellEntriesContainer26, calendarCellEntriesContainer36,
			calendarCellEntriesContainer46, calendarCellEntriesContainer56, calendarCellEntriesContainer66;
	@FXML
	private JFXButton createEventBtn, selectEventBtn, leftMonthBtn, rightMonthBtn;
	@FXML
	private StackPane monthLeftArrowContainer;
	@FXML
	private StackPane monthRightArrowContainer;
	@FXML
	private JFXComboBox<Integer> yearComboBox;
	@FXML
	private Label monthLabel;

	/****************************** Variablen ******************************/
	private ArrayList<AnchorPane> anchorPaneList = new ArrayList<AnchorPane>();
	private ArrayList<Label> daysNumberLabelList = new ArrayList<Label>();
	private ArrayList<VBox> calendarCellEntriesList = new ArrayList<VBox>();
	private Calendar calendar = DateTime.getCalearndarInstance();
	@Setter
	private static MainViewController mainViewController;
	@Setter
	private static HomeController homeController;
	@Setter
	private static User user;
	private static List<Event> createdEvents;
	private static List<Event> upcommingEvents;
	@Setter
	private static List<Event> allEvents;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setStyle();
		setIcons();
		initializeLists();
		initializeYearComboBox();
		initializeMonth();
		initializeCalendar();

	}


	/**
	 * YearComboBox ab Jahr 2021 bis zu einem vorgegebenen Jahr initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void initializeYearComboBox() {
		for (int year = 2021; year <= MAX_YEAR; year++) {
			yearComboBox.getItems().add(year);
		}
		yearComboBox.setValue(DateTime.getCurrentYear());
		yearComboBxOnFocus();
	}

	/**
	 * Alle offene Boxes schliessen, wenn {@link #yearComboBox} angeklickt
	 * wurde.
	 * 
	 * @author Feras Ejneid
	 */
	private void yearComboBxOnFocus() {
		yearComboBox.focusedProperty().addListener(y -> {
			mainViewController.hideNotifications();
			mainViewController.hideSettings();
		});
	}

	/**
	 * {@link #monthLabel} mit der Nummer des aktuellen Monats initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void initializeMonth() {
		monthLabel.setText(DateTime.getCurrentMonth());
	}

	/**
	 * ein Dialogfenster zum Erstellen eines neuen Termins anzeigen.
	 * 
	 * @param event
	 * @author Feras Ejneid
	 * @throws IOException
	 */
	@FXML
	void createNewEvent(ActionEvent event) throws IOException {
		NewEventController.setUser(user);
		NewEventController.setCreatedEvents(createdEvents);
		FXMLLoader fxmlLoader = new FXMLLoader();
		DialogPane dialogLoader = fxmlLoader.load(getClass().getResource(Constants.NEW_EVENT_FXML).openStream());
		NewEventController newEventController = (NewEventController) fxmlLoader.getController();
		newEventController.setHomeController(homeController);
		Dialog<Object> addDialog = createDialog(dialogLoader);
		addDialog.showAndWait();
	}

	/**
	 * ein Fenster mit den verfuegbaren Terminen anzeigen.
	 * 
	 * @param event
	 * @throws IOException
	 * @author Feras Ejneid
	 */
	@FXML
	void selectEvent(ActionEvent event) throws IOException {
		AllEventsController.setUser(user);
		FXMLLoader fxmlLoader = new FXMLLoader();
		Parent root = fxmlLoader.load(LoginController.class.getResource(Constants.ALL_EVENTS_FXML).openStream());
		AllEventsController allEventsController = (AllEventsController) fxmlLoader.getController();
		allEventsController.setHomeController(homeController);
		createStage(root);
	}

	/*
	 * Erstellt einen Dialogfenstercontainer.
	 */

	private Dialog<Object> createDialog(DialogPane dialogLoader) {
		Dialog<Object> dialog = new Dialog<Object>();
		dialog.initStyle(StageStyle.TRANSPARENT);
		dialog.getDialogPane().getScene().setFill(Color.TRANSPARENT);
		abilityToCloseFromXButton(dialog);

		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		ResizeHelper.addResizeListener(stage);

		dialog.setDialogPane(dialogLoader);
		return dialog;
	}

	/*
	 * neue Buehne schaffen.
	 */

	private void createStage(Parent root) {
		Scene scene = new Scene(root);
		Stage mainStage = new Stage();
		mainStage.setScene(scene);
		mainStage.setTitle(Constants.STAGE_TITLE);
		mainStage.setMinWidth(1290.0);
		mainStage.setMinHeight(800.0);
		mainStage.setScene(scene);
		mainStage.show();
	}

	/**
	 * Schliesst die Buehne ueber den "X"-Button.
	 * 
	 * @param dialog
	 * 
	 * @author Feras Ejneid
	 */
	private void abilityToCloseFromXButton(final Dialog<Object> dialog) {
		Window window = dialog.getDialogPane().getScene().getWindow();
		window.setOnCloseRequest(e -> dialog.hide());
	}

	/**
	 * den aktuell angezeigten Monatsnamen in den vorherigen aendern. Wenn der
	 * aktuelle Monat Januar ist, ist der vorherige Monat Dezember.
	 * 
	 * {@link #getSelectedMonthNumber()} gibt eine Monatsnummer zwischen 0 und
	 * 11 -z.B Janaur hat die Nummer 0- zurueck, waehrend die LocalDate-Klasse,
	 * die in der Methode {@link DateTime#getNameOfPrevtMonth(int,int)}
	 * verwendet wurde, Werte zwischen 1-12 gibt -z.B Januar hat die Nummer 1-.
	 * Deshalb -> getSelectedMonthNumber() + 1
	 * 
	 * @param event
	 * 
	 * @author Feras Ejneid
	 */
	@FXML
	void getPrevMonth(ActionEvent event) {
		int numberOfselectedMonth = getSelectedMonthNumber() + 1;
		int selectedYear = getSelectedYear();
		String monthName = DateTime.getNameOfPrevtMonth(selectedYear, numberOfselectedMonth);

		monthLabel.setText(monthName);

		initializeCalendar();
	}

	/**
	 * den aktuell angezeigten Monatsnamen in den naechsten kommenden aendern.
	 * Wenn der aktuelle Monat Dezember ist, ist der naechste Monat Januar.
	 *
	 * getSelectedMonthNumber() gibt eine Monatsnummer zwischen 0 und 11 -z.B
	 * Janaur hat die Nummer 0- zurueck, waehrend die LocalDate-Klasse, die in
	 * der Methode {@link DateTime#getNameOfNextMonth(int,int)} verwendet wurde,
	 * Werte zwischen 1-12 gibt - z.B Januar hat die Nummer 1-. Deshalb ->
	 * getSelectedMonthNumber() + 1
	 * 
	 * @param event
	 * 
	 * @author Feras Ejneid
	 */
	@FXML
	void getNextMonth(ActionEvent event) {
		int numberOfselectedMonth = getSelectedMonthNumber() + 1;
		int selectedYear = getSelectedYear();
		String monthName = DateTime.getNameOfNextMonth(selectedYear, numberOfselectedMonth);

		monthLabel.setText(monthName);

		initializeCalendar();
	}

	/**
	 * die Kalenderansicht nach Auswahl des neuen Jahres neu initialisieren.
	 * 
	 * @param event
	 * 
	 * @author Feras Ejneid
	 */
	@FXML
	void reselectYear(ActionEvent event) {
		initializeCalendar();
		// notificationPane.setVisible(false);
		// settingsPane.setVisible(false);
	}

	/**
	 * Kalendersicht initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	public void initializeCalendar() {
		resetAll();
		initializeCalendarCells();
	}

	/**
	 * die Tagesnummer in den Kalenderzellen zuruecksetzen und einen Kreis um
	 * jede Nummer zeichnen, aber ihn versteckt halten.
	 * 
	 * @author Feras Ejneid
	 */
	private void resetAll() {
		for (AnchorPane anchorPane : anchorPaneList) {
			((Label) anchorPane.getChildren().get(0)).setText("");
			((Circle) anchorPane.getChildren().get(2)).setRadius(13.5);
			((Circle) anchorPane.getChildren().get(2)).setVisible(false);
		}

		for (VBox vbox : calendarCellEntriesList)
			vbox.getChildren().clear();

	}

	/**
	 * 
	 * Kalenderzellen initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void initializeCalendarCells() {
		int firstDay = DateTime.getFirstDayOfMonth(getSelectedMonthNumber(), getSelectedYear());
		String firstDayLabel = getLabelNameOfAGivenDayNumber(firstDay);
		int days = DateTime.getNumberOfDays(getSelectedYear(), getSelectedMonthNumber());
		initEachCalendarCell(firstDayLabel, days);
	}

	/**
	 * 
	 * Kalenderzellen durchnummerieren und mit Terminen initialisieren.
	 * 
	 * @param firstDayLabel
	 *            ab dem beginnt die Nummerierung.
	 * @param days
	 *            Anzahl der Tagen des anzuzeigenden Monats.
	 * 
	 * @author Feras Ejneid
	 */
	private void initEachCalendarCell(String firstDayLabel, int days) {
		// Index der ersten Kalenderzelle, die nummeriert werden kann
		int firstIndex = -1;

		// Der Kalender besteht aus 42 Zellen
		for (int currentIndex = 0; currentIndex < 42; currentIndex++) {
			Label currentDayNumberLabel = daysNumberLabelList.get(currentIndex);
			String dayNumberLabelId = currentDayNumberLabel.getId();

			if (dayNumberLabelId.equals(firstDayLabel)) {
				firstIndex = currentIndex;
				initFirstInitializableCalendarCell(currentDayNumberLabel);
			} else if (currentIndex < firstIndex || firstIndex == -1) {
				currentDayNumberLabel.setText("");
			} else if (currentIndex >= firstIndex + days) {
				currentDayNumberLabel.setText("");
			} else {
				initNormalCalendarCells(firstIndex, currentIndex, currentDayNumberLabel);
			}
		}
	}

	/**
	 * 
	 * Alle Kalederzellen, die zu den Monatstagen zwischen dem zweiten Tag des
	 * Monats und dem letzten Tag des Monats gehoeren., initialisieren.
	 * 
	 * Die Kalenderzelle des ersten Tag des Monats ist durch
	 * {@link #initFirstInitializableCalendarCell(Label)} Methode initialisiert.
	 * Die anderen Kalenderzellen, die zu keinem Tag des Monats gehoeren, werden
	 * leer bleiben.
	 * 
	 * @param firstIndex
	 * @param currentIndex
	 * @param currentDayNumberLabel
	 * 
	 * @author Feras Ejneid
	 */
	private void initNormalCalendarCells(int firstIndex, int currentIndex, Label currentDayNumberLabel) {
		currentDayNumberLabel.setText((currentIndex - firstIndex + 1) + "");
		int dayNumber = Integer.parseInt(currentDayNumberLabel.getText());
		if (checkToday(dayNumber)) {
			AnchorPane parentOfLabel = (AnchorPane) currentDayNumberLabel.getParent();
			parentOfLabel.getChildren().get(2).setVisible(true);
		}
		addEventsEntries(currentDayNumberLabel, dayNumber);

	}

	/**
	 * einen Termineintrag in den Container, der die gleiche ID wie
	 * {@link currentDayNumberLabel} hat, einsetzen, wenn die Tagesnummer, der
	 * Monat und das Jahr des Termineintrags mit der Nummer des Containers , dem
	 * angezeigten Monat und dem angezeigten Jahr uebereinstimmen.
	 * 
	 *
	 *
	 * @param currentDayNumberLabel
	 * @param dayNumber
	 *
	 * @author Feras Ejneid
	 * @throws IOException
	 */
	private void addEventsEntries(Label currentDayNumberLabel, int dayNumber) {
		List<Event> eventsForCell = allEvents.stream().filter(event -> {
			Date eventDate = event.getDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(eventDate);
			return calendar.get(Calendar.DAY_OF_MONTH) == dayNumber
					&& calendar.get(Calendar.MONTH) == getSelectedMonthNumber()
					&& calendar.get(Calendar.YEAR) == getSelectedYear();
		}).collect(Collectors.toList());

		String calendarCellEntriesContainerId = getNameOfCellContainerFromDayNumberLabel(currentDayNumberLabel);
		eventsForCell.forEach(event -> addEventEntryInContainer(calendarCellEntriesContainerId, event.getTitle()));

		// try {
		// createdEvents =
		// UserRequest.findCreatedEvents(user.getUsername()).stream().filter(event
		// -> {
		// Date eventDate = event.getDate();
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(eventDate);
		// return calendar.get(Calendar.DAY_OF_MONTH) == dayNumber
		// && calendar.get(Calendar.MONTH) == getSelectedMonthNumber()
		// && calendar.get(Calendar.YEAR) == getSelectedYear();
		// }).collect(Collectors.toList());
		//
		// List<Event> incommingEvents =
		// UserRequest.findIncommingEvents(user.getUsername()).stream().filter(event
		// -> {
		// Date eventDate = event.getDate();
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(eventDate);
		// return calendar.get(Calendar.DAY_OF_MONTH) == dayNumber
		// && calendar.get(Calendar.MONTH) == getSelectedMonthNumber()
		// && calendar.get(Calendar.YEAR) == getSelectedYear();
		// }).collect(Collectors.toList());
		//
		// List<Event> events = new ArrayList<Event>(createdEvents.size() +
		// incommingEvents.size());
		// events.addAll(createdEvents);
		// events.addAll(incommingEvents);
		// String calendarCellEntriesContainerId =
		// getNameOfCellContainerFromDayNumberLabel(currentDayNumberLabel);
		// events.forEach(event ->
		// addEventEntryInContainer(calendarCellEntriesContainerId));
		// // System.out.println("DayNumber -> " + dayNumber + "\n" + events);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	/**
	 * 
	 * einen Termineintrag in den Container mit der angegebenen ID einsetzen.
	 *
	 * @param calendarCellEntriesContainerId
	 *
	 * @author Feras Ejneid
	 */
	private void addEventEntryInContainer(String calendarCellEntriesContainerId, String eventTitle) {
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			CalendarEventCellController.setTitle(eventTitle);
			Node calendarCellEntry = fxmlLoader
					.load(getClass().getResource(Constants.CALENDER_CELL_ENTRY_FXML).openStream());

			// den Container mit der angegebenen ID suchen
			VBox eventsEntriesContainer = calendarCellEntriesList.stream()
					.filter(entry -> entry.getId().equals(calendarCellEntriesContainerId)).findFirst().get();
			// einen Termineintrag hinzufuegen
			eventsEntriesContainer.getChildren().add(calendarCellEntry);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 
	 * Jede Kalenderzelle besteht aus einem Root, einem Label, in dem die Nummer
	 * der Monatstagen eingetragen werden, einer VBox, in dem die Titel der
	 * Termine mit der Nummer im Label eingetragen werden und einem Circle, um
	 * die Nummer, die mit dem heutigen Monatstag uebereinstimmt, zu selektiern.
	 * Diese Methode gibt den Namen des dritten Elements in der Kalenderzelle
	 * an, naemlich VBox , um in dem die Titel der Termine einzutragen.
	 *
	 * @param currentDayNumberLabel
	 * @return String die Name vom Container in einer Kalender Zelle, wo Titel
	 *         von Terminen eingetragen werden.
	 *
	 * @author Feras Ejneid
	 */
	private String getNameOfCellContainerFromDayNumberLabel(Label currentDayNumberLabel) {
		String cellPositionInCalendar = currentDayNumberLabel.getId().replaceAll("[a-zA-z]", "");
		String calendarCellEntriesContainerId = "calendarCellEntriesContainer" + cellPositionInCalendar;
		return calendarCellEntriesContainerId;
	}

	/**
	 * Die erste Zelle in der Kalenderansicht, die nummeriert werden kann,
	 * initialisieren.
	 * 
	 * @param dayNumberLabel
	 *            in jeder Kalenderzelle gibt es ein Label (dayNumberLabel) fuer
	 *            die Nummer der Monatstage
	 * 
	 * @author Feras Ejneid
	 */
	private void initFirstInitializableCalendarCell(Label dayNumberLabel) {
		dayNumberLabel.setText("1");
		int dayNumber = Integer.parseInt(dayNumberLabel.getText());
		if (checkToday(dayNumber)) {
			AnchorPane parentOfLabel = (AnchorPane) dayNumberLabel.getParent();
			// einen Kreis um die Tagesnummer zeichnen, wenn es heute
			// ist
			parentOfLabel.getChildren().get(2).setVisible(true);
		}
		addEventsEntries(dayNumberLabel, dayNumber);
	}

	/**
	 * die Nummer des Namens des ersten Tages in einem Monat nehmen und den
	 * Namen der zugehoerigen richtigen Kalenderzelle zurueckgeben, ab der der
	 * Monat durchnummeriert wird.
	 * 
	 * Z.B: ist die angegebne Nummer 1 (Sonntag), muss die Nummerierung, ab der
	 * Kalenderzelle mit der Koordination (0,1) beginnen, da diese Zelle die
	 * erste Zelle ist, die nummeriert werden kann. Wenn die angegebne Nummer 2
	 * (Montag) ist, muss ab der Kalenderzelle mit der Koordination (1,1)
	 * begonnen werden usw.
	 * 
	 * @param firstDay
	 * @return String Der Name der Kalenderzelle, ab der die Nummerierung
	 *         beginnt.
	 * 
	 * @author Feras Ejneid
	 */
	private String getLabelNameOfAGivenDayNumber(int firstDay) {
		String firstDayLabel = "";

		switch (firstDay) {
		case Calendar.SUNDAY:
			firstDayLabel = "numberOfDayLabel01";
			break;
		case Calendar.MONDAY:
			firstDayLabel = "numberOfDayLabel11";
			break;
		case Calendar.TUESDAY:
			firstDayLabel = "numberOfDayLabel21";
			break;
		case Calendar.WEDNESDAY:
			firstDayLabel = "numberOfDayLabel31";
			break;
		case Calendar.THURSDAY:
			firstDayLabel = "numberOfDayLabel41";
			break;
		case Calendar.FRIDAY:
			firstDayLabel = "numberOfDayLabel51";
			break;
		case Calendar.SATURDAY:
			firstDayLabel = "numberOfDayLabel61";
			break;

		default:
			break;
		}

		return firstDayLabel;
	}

	/**
	 * ueberpruefen, ob die uebergebene Tagesnummer heute ist.
	 * 
	 * @param dayNumber
	 * @return true , wenn die Nummer von heute gleich die uebergebene Nummer
	 *         ist.
	 * 
	 * @author Feras Ejneid
	 */
	private boolean checkToday(int dayNumber) {
		Calendar tempCalendar = Calendar.getInstance();
		Date today = tempCalendar.getTime();

		int dayOfMonth = dayNumber;
		int month = getSelectedMonthNumber();
		int year = getSelectedYear();

		tempCalendar.set(Calendar.YEAR, year);
		tempCalendar.set(Calendar.MONTH, month);
		tempCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

		Date dateSpecified = tempCalendar.getTime();

		return today.equals(dateSpecified);
	}

	/**
	 * den aktuell angezeigten Monatsnamen von {@link #monthLabel} nehmen und
	 * die entsprechende Monatsnummer zurueckgeben.
	 * 
	 * @return int Monatsnummer
	 * 
	 * @author Feras Ejneid
	 */
	private int getSelectedMonthNumber() {
		int selectedMonthNumber = -1;

		String selectedMonthName = monthLabel.getText();
		Date date;
		try {
			date = new SimpleDateFormat("MMMM", Locale.GERMAN).parse(selectedMonthName);
			calendar.setTime(date);
			selectedMonthNumber = calendar.get(Calendar.MONTH);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return selectedMonthNumber;
	}

	/**
	 * das ausgewaehlte Jahr im {@link #yearComboBox} zurueckgeben.
	 * 
	 * @return int das ausgewaehlte Jahr
	 * 
	 * @author Feras Ejneid
	 */
	private int getSelectedYear() {
		return yearComboBox.getSelectionModel().getSelectedItem();
	}

	/**
	 *
	 * Labels und Panes zu den Listten hinzufuegen, sodass es einfacher ist, mit
	 * denenen zu arbeiten.
	 *
	 * @author Feras Ejneid
	 */
	private void initializeLists() {
		daysNumberLabelList.add(numberOfDayLabel01);
		daysNumberLabelList.add(numberOfDayLabel11);
		daysNumberLabelList.add(numberOfDayLabel21);
		daysNumberLabelList.add(numberOfDayLabel31);
		daysNumberLabelList.add(numberOfDayLabel41);
		daysNumberLabelList.add(numberOfDayLabel51);
		daysNumberLabelList.add(numberOfDayLabel61);

		daysNumberLabelList.add(numberOfDayLabel02);
		daysNumberLabelList.add(numberOfDayLabel12);
		daysNumberLabelList.add(numberOfDayLabel22);
		daysNumberLabelList.add(numberOfDayLabel32);
		daysNumberLabelList.add(numberOfDayLabel42);
		daysNumberLabelList.add(numberOfDayLabel52);
		daysNumberLabelList.add(numberOfDayLabel62);

		daysNumberLabelList.add(numberOfDayLabel03);
		daysNumberLabelList.add(numberOfDayLabel13);
		daysNumberLabelList.add(numberOfDayLabel23);
		daysNumberLabelList.add(numberOfDayLabel33);
		daysNumberLabelList.add(numberOfDayLabel43);
		daysNumberLabelList.add(numberOfDayLabel53);
		daysNumberLabelList.add(numberOfDayLabel63);

		daysNumberLabelList.add(numberOfDayLabel04);
		daysNumberLabelList.add(numberOfDayLabel14);
		daysNumberLabelList.add(numberOfDayLabel24);
		daysNumberLabelList.add(numberOfDayLabel34);
		daysNumberLabelList.add(numberOfDayLabel44);
		daysNumberLabelList.add(numberOfDayLabel54);
		daysNumberLabelList.add(numberOfDayLabel64);

		daysNumberLabelList.add(numberOfDayLabel05);
		daysNumberLabelList.add(numberOfDayLabel15);
		daysNumberLabelList.add(numberOfDayLabel25);
		daysNumberLabelList.add(numberOfDayLabel35);
		daysNumberLabelList.add(numberOfDayLabel45);
		daysNumberLabelList.add(numberOfDayLabel55);
		daysNumberLabelList.add(numberOfDayLabel65);

		daysNumberLabelList.add(numberOfDayLabel06);
		daysNumberLabelList.add(numberOfDayLabel16);
		daysNumberLabelList.add(numberOfDayLabel26);
		daysNumberLabelList.add(numberOfDayLabel36);
		daysNumberLabelList.add(numberOfDayLabel46);
		daysNumberLabelList.add(numberOfDayLabel56);
		daysNumberLabelList.add(numberOfDayLabel66);

		anchorPaneList.add(anchorPane01);
		anchorPaneList.add(anchorPane11);
		anchorPaneList.add(anchorPane21);
		anchorPaneList.add(anchorPane31);
		anchorPaneList.add(anchorPane41);
		anchorPaneList.add(anchorPane51);
		anchorPaneList.add(anchorPane61);

		anchorPaneList.add(anchorPane02);
		anchorPaneList.add(anchorPane12);
		anchorPaneList.add(anchorPane22);
		anchorPaneList.add(anchorPane32);
		anchorPaneList.add(anchorPane42);
		anchorPaneList.add(anchorPane52);
		anchorPaneList.add(anchorPane62);

		anchorPaneList.add(anchorPane03);
		anchorPaneList.add(anchorPane13);
		anchorPaneList.add(anchorPane23);
		anchorPaneList.add(anchorPane33);
		anchorPaneList.add(anchorPane43);
		anchorPaneList.add(anchorPane53);
		anchorPaneList.add(anchorPane63);

		anchorPaneList.add(anchorPane04);
		anchorPaneList.add(anchorPane14);
		anchorPaneList.add(anchorPane24);
		anchorPaneList.add(anchorPane34);
		anchorPaneList.add(anchorPane44);
		anchorPaneList.add(anchorPane54);
		anchorPaneList.add(anchorPane64);

		anchorPaneList.add(anchorPane05);
		anchorPaneList.add(anchorPane15);
		anchorPaneList.add(anchorPane25);
		anchorPaneList.add(anchorPane35);
		anchorPaneList.add(anchorPane45);
		anchorPaneList.add(anchorPane55);
		anchorPaneList.add(anchorPane65);

		anchorPaneList.add(anchorPane06);
		anchorPaneList.add(anchorPane16);
		anchorPaneList.add(anchorPane26);
		anchorPaneList.add(anchorPane36);
		anchorPaneList.add(anchorPane46);
		anchorPaneList.add(anchorPane56);
		anchorPaneList.add(anchorPane66);

		calendarCellEntriesList.add(calendarCellEntriesContainer01);
		calendarCellEntriesList.add(calendarCellEntriesContainer11);
		calendarCellEntriesList.add(calendarCellEntriesContainer21);
		calendarCellEntriesList.add(calendarCellEntriesContainer31);
		calendarCellEntriesList.add(calendarCellEntriesContainer41);
		calendarCellEntriesList.add(calendarCellEntriesContainer51);
		calendarCellEntriesList.add(calendarCellEntriesContainer61);

		calendarCellEntriesList.add(calendarCellEntriesContainer02);
		calendarCellEntriesList.add(calendarCellEntriesContainer12);
		calendarCellEntriesList.add(calendarCellEntriesContainer22);
		calendarCellEntriesList.add(calendarCellEntriesContainer32);
		calendarCellEntriesList.add(calendarCellEntriesContainer42);
		calendarCellEntriesList.add(calendarCellEntriesContainer52);
		calendarCellEntriesList.add(calendarCellEntriesContainer62);

		calendarCellEntriesList.add(calendarCellEntriesContainer03);
		calendarCellEntriesList.add(calendarCellEntriesContainer13);
		calendarCellEntriesList.add(calendarCellEntriesContainer23);
		calendarCellEntriesList.add(calendarCellEntriesContainer33);
		calendarCellEntriesList.add(calendarCellEntriesContainer43);
		calendarCellEntriesList.add(calendarCellEntriesContainer53);
		calendarCellEntriesList.add(calendarCellEntriesContainer63);

		calendarCellEntriesList.add(calendarCellEntriesContainer04);
		calendarCellEntriesList.add(calendarCellEntriesContainer14);
		calendarCellEntriesList.add(calendarCellEntriesContainer24);
		calendarCellEntriesList.add(calendarCellEntriesContainer34);
		calendarCellEntriesList.add(calendarCellEntriesContainer44);
		calendarCellEntriesList.add(calendarCellEntriesContainer54);
		calendarCellEntriesList.add(calendarCellEntriesContainer64);

		calendarCellEntriesList.add(calendarCellEntriesContainer05);
		calendarCellEntriesList.add(calendarCellEntriesContainer15);
		calendarCellEntriesList.add(calendarCellEntriesContainer25);
		calendarCellEntriesList.add(calendarCellEntriesContainer35);
		calendarCellEntriesList.add(calendarCellEntriesContainer45);
		calendarCellEntriesList.add(calendarCellEntriesContainer55);
		calendarCellEntriesList.add(calendarCellEntriesContainer65);

		calendarCellEntriesList.add(calendarCellEntriesContainer06);
		calendarCellEntriesList.add(calendarCellEntriesContainer16);
		calendarCellEntriesList.add(calendarCellEntriesContainer26);
		calendarCellEntriesList.add(calendarCellEntriesContainer36);
		calendarCellEntriesList.add(calendarCellEntriesContainer46);
		calendarCellEntriesList.add(calendarCellEntriesContainer56);
		calendarCellEntriesList.add(calendarCellEntriesContainer66);
	}

	/**
	 * den Stil der Komponenten initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void setStyle() {
		monthLabel.setStyle(Constants.FONT_BOLD_24);
		yearComboBox.setStyle(Constants.FONT_BOLD_24);

		sunday.setStyle(Constants.FONT_BOLD_18);
		monday.setStyle(Constants.FONT_BOLD_18);
		tuesday.setStyle(Constants.FONT_BOLD_18);
		wednesday.setStyle(Constants.FONT_BOLD_18);
		thursday.setStyle(Constants.FONT_BOLD_18);
		friday.setStyle(Constants.FONT_BOLD_18);
		saturday.setStyle(Constants.FONT_BOLD_18);

		DropShadow shadow = new DropShadow();
		shadow.setBlurType(BlurType.GAUSSIAN);
		shadow.setRadius(7);
		shadow.setColor(Color.DARKGREY);

		calendarPane.setEffect(shadow);
	}

	/**
	 * Symbole hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void setIcons() {
		MaterialIconView leftArrow = new MaterialIconView(MaterialIcon.KEYBOARD_ARROW_LEFT);
		leftArrow.setSize(Constants.SIZE_2EM);
		monthLeftArrowContainer.getChildren().add(leftArrow);

		MaterialIconView rightArrow = new MaterialIconView(MaterialIcon.KEYBOARD_ARROW_RIGHT);
		rightArrow.setSize(Constants.SIZE_2EM);
		monthRightArrowContainer.getChildren().add(rightArrow);

		MaterialDesignIconView calendarIcon = new MaterialDesignIconView(MaterialDesignIcon.CALENDAR_TODAY);
		calendarIcon.setSize(Constants.SIZE_2EM);
		// calendarIconContainer.getChildren().add(calendarIcon);
	}
}
