package de.htwsaar.pib.vs.zms.client.controller.homecontroller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.eventcontroller.InvitableUserEntryController;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.MainViewController;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Setter;

public class TodaysEventsController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private AnchorPane todaysEventsRoot;
	@FXML
	private Label todayLabel;
	@FXML
	private VBox todayEventsCardsContainer;
	@FXML
	private StackPane eventStatusIconContainer, calendarIconContainer;

	@Setter
	private static MainViewController mainViewController;
	@Setter
	private static User user;
	@Setter
	private static List<Event> allEvents;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		setStyle();
		setIcons();
		initializeTodayEvents();
	}

	private void initializeTodayEvents() {
		List<Event> todayEvents = allEvents.stream().filter(event -> {
			Date today = Calendar.getInstance().getTime();
			return isSameDay(today, event.getDate());
		}).collect(Collectors.toList());

		todayEvents.forEach(event -> addTodayEventEntryInContainer(String.valueOf(event.getDuration()),
				event.getTitle(), event.getDate().toString()));
	}

	public static boolean isSameDay(Date date1, Date date2) {
		LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return localDate1.isEqual(localDate2);
	}

	private void addTodayEventEntryInContainer(String startTime, String eventTitle, String startEndTime) {
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			TodaysEventEntryController.setStartTimeValue(startTime + " Min");
			TodaysEventEntryController.setEventTitleValue(eventTitle);
			TodaysEventEntryController.setStartEndTimeValue(startEndTime);
			Node todaysEventEntry = fxmlLoader
					.load(getClass().getResource(Constants.TODAYS_EVENT_ENTRY_FXML).openStream());

			todayEventsCardsContainer.getChildren().add(todaysEventEntry);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
		todaysEventsRoot.setEffect(shadow);
	}

	/**
	 * Symbole hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void setIcons() {
		MaterialDesignIconView calendarIcon = new MaterialDesignIconView(MaterialDesignIcon.CALENDAR_TODAY);
		calendarIcon.setSize(Constants.SIZE_2EM);
		calendarIconContainer.getChildren().add(calendarIcon);
	}
}
