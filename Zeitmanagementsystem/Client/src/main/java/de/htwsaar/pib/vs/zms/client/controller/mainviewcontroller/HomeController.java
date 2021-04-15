package de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXCheckBox;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.homecontroller.CalendarController;
import de.htwsaar.pib.vs.zms.client.controller.homecontroller.TodaysEventsController;
import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Setter;

public class HomeController implements Initializable {

	/****************************** FX Attribute ******************************/

	@FXML
	private AnchorPane homeRoot;
	@FXML
	private GridPane mainGrid, leftGrid;
	@FXML
	private AnchorPane calendarPane, toolsPane, eventTypePane, todaysEventsPane;
	@FXML
	private StackPane settingsIconContainer, notificationsIconContainer, eventStatusIconContainer, calendarIconContaine,
			categoryIconContainer, calendarIconContainer, settingsArrowContainer, notificationsArrowContainer;

	@FXML
	private Label workCategoryLabel, privateCategoryLabel, othersCategoryLabel, selectDeselectLabel,
			eventsCategoryLabel, todayLabel;
	@FXML
	private JFXCheckBox workCategoryCheckBox, privateCategoryCheckBox, othersCategoryCheckBox,
			selectAllCategoriesCheckBox;
	@FXML
	private VBox todayEventsCardsContainer;

	/****************************** Variablen ******************************/
	private static MainViewController mainViewController;
	private static List<Event> allEvents;

	@Setter
	private static User user;
	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setStyle();
		initializeAllEvents();
		initializeTodaysEvents();
		initializeCalendar();
	}

	/****************************** Initialisierung ******************************/

	/**
	 * die Kalendersicht in die Hauptseite einsetzen.
	 * 
	 * @author Feras Ejneid
	 */
	public void initializeCalendar() {
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			CalendarController.setHomeController(this);
			CalendarController.setUser(user);
			CalendarController.setAllEvents(allEvents);
			Node calendar = fxmlLoader.load(getClass().getResource(Constants.CALENDAR_FXML).openStream());
			calendarPane.getChildren().add(calendar);
			AnchorPane.setLeftAnchor(calendar, 0.0);
			AnchorPane.setTopAnchor(calendar, 0.0);
			AnchorPane.setRightAnchor(calendar, 0.0);
			AnchorPane.setBottomAnchor(calendar, 0.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 */
	public void initializeAllEvents() {
		user = MainViewController.getUser();
		try {
			allEvents = ServiceFacade.findAllEvents();
			// TODO alle eingehende Einladungen abfragen
			// upcommingEvents = ServiceFacade.findUpcomingEvents(user);
//			allEvents = new ArrayList<Event>(createdEvents.size());
//			allEvents.addAll(createdEvents);
			// allEvents.addAll(upcommingEvents);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * die heutigen Termine einsetzen.
	 * 
	 * @author Feras Ejneid
	 */
	public void initializeTodaysEvents() {
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			TodaysEventsController.setUser(user);
			TodaysEventsController.setAllEvents(allEvents);
			Node todaysEvents = fxmlLoader.load(getClass().getResource(Constants.TODAYS_EVENTS_FXML).openStream());
			leftGrid.add(todaysEvents, 0, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MainViewController getMainViewController() {
		return mainViewController;
	}

	public static void setMainViewController(MainViewController mainViewController) {
		HomeController.mainViewController = mainViewController;
	}

	/**
	 * den Stil der Komponenten initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void setStyle() {
		DropShadow shadow = new DropShadow();
		shadow.setBlurType(BlurType.GAUSSIAN);
		shadow.setRadius(7);
		shadow.setColor(Color.DARKGREY);
		eventTypePane.setEffect(shadow);
		// todaysEventsPane.setEffect(shadow);
	}
}
