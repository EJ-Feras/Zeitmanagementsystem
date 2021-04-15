package de.htwsaar.pib.vs.zms.client.controller.eventcontroller;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.DeleteController.DeletionController;
import de.htwsaar.pib.vs.zms.client.controller.homecontroller.CalendarController;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.HomeController;
import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.utils.ResizeHelper;
import de.htwsaar.pib.zms.server.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;

public class AllEventsController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private StackPane allEventsRoot;
	@FXML
	private AnchorPane toolsPane, calendarPane;
	@FXML
	private ImageView manWithPenImgView;
	@FXML
	private TableView<de.htwsaar.pib.zms.server.model.Event> eventsTable;
	@FXML
	private TableColumn<de.htwsaar.pib.zms.server.model.Event, String> titleCol;
	@FXML
	private TableColumn<de.htwsaar.pib.zms.server.model.Event, LocalDate> dateCol;
	@FXML
	private TableColumn<de.htwsaar.pib.zms.server.model.Event, LocalDateTime> startTimeCol;
	@FXML
	private TableColumn<de.htwsaar.pib.zms.server.model.Event, Integer> durationCol;
	@FXML
	private TableColumn<de.htwsaar.pib.zms.server.model.Event, HBox> actionCol;

	@Getter
	@Setter
	private static User user;
	@Setter
	private static List<de.htwsaar.pib.zms.server.model.Event> allEvents;
	@Setter
	private HomeController homeController;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setStyle();
		initializeEventsTable();
	}

	/**
	 * die Tabelle aller Termine initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void initializeEventsTable() {
		try {
			allEvents = ServiceFacade.findAllEvents();
		} catch (NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		eventsTable.setItems(FXCollections.observableList(allEvents));

		titleCol.setCellValueFactory(new PropertyValueFactory<de.htwsaar.pib.zms.server.model.Event, String>("title"));
		dateCol.setCellFactory(column -> new DateColumnCallback().call(column));
		startTimeCol.setCellFactory(column -> new StartTimeColumnCallback().call(column));
		durationCol.setCellValueFactory(
				new PropertyValueFactory<de.htwsaar.pib.zms.server.model.Event, Integer>("duration"));
		actionCol.setCellFactory(column -> new ActionsColumnCallback().call(column));
	}

	/**
	 * die Tabelle aller Termine aktualisieren.
	 * 
	 * @author Feras Ejneid
	 */
	public void refreshEventsTable() {
		eventsTable.getItems().clear();
		initializeEventsTable();

	}

	private void removeEvent(de.htwsaar.pib.zms.server.model.Event event) {
		FXMLLoader dialogLoader = new FXMLLoader();
		DialogPane confirmDeletion;
		try {
			confirmDeletion = dialogLoader.load(getClass().getResource(Constants.CONFIRM_DELETION_FXML).openStream());
			DeletionController deletionController = (DeletionController) dialogLoader.getController();

			deletionController.setEvent(event);

			Dialog<Object> removeDialog = createDialog(confirmDeletion);
			abilityToCloseFromXButton(removeDialog);
			removeDialog.showAndWait();

			refreshEventsTable();
			homeController.initializeAllEvents();
			homeController.initializeCalendar();
			homeController.initializeTodaysEvents();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * 
	 * teilt der Spalte mit, wie das Datum angezeigt werden soll.
	 *
	 */
	private class DateColumnCallback implements
			Callback<TableColumn<de.htwsaar.pib.zms.server.model.Event, LocalDate>, TableCell<de.htwsaar.pib.zms.server.model.Event, LocalDate>> {

		public TableCell<de.htwsaar.pib.zms.server.model.Event, LocalDate> call(
				TableColumn<de.htwsaar.pib.zms.server.model.Event, LocalDate> column) {

			return new TableCell<de.htwsaar.pib.zms.server.model.Event, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);
					TableRow<de.htwsaar.pib.zms.server.model.Event> tableRow = getTableRow();

					if (tableRow != null) {
						final de.htwsaar.pib.zms.server.model.Event tmp = tableRow.getItem();
						if (tmp != null) {
							setText(convertToLocalDateViaInstant(tmp.getDate()).toString());
						}
					}
				}
			};
		}
	}

	/*
	 * 
	 * teilt der Spalte mit, wie die Uhrzeit angezeigt werden soll.
	 *
	 */
	private class StartTimeColumnCallback implements
			Callback<TableColumn<de.htwsaar.pib.zms.server.model.Event, LocalDateTime>, TableCell<de.htwsaar.pib.zms.server.model.Event, LocalDateTime>> {

		public TableCell<de.htwsaar.pib.zms.server.model.Event, LocalDateTime> call(
				TableColumn<de.htwsaar.pib.zms.server.model.Event, LocalDateTime> column) {

			return new TableCell<de.htwsaar.pib.zms.server.model.Event, LocalDateTime>() {
				@Override
				protected void updateItem(LocalDateTime item, boolean empty) {
					super.updateItem(item, empty);
					TableRow<de.htwsaar.pib.zms.server.model.Event> tableRow = getTableRow();

					if (tableRow != null) {
						final de.htwsaar.pib.zms.server.model.Event tmp = tableRow.getItem();
						if (tmp != null) {
							setText(convertToLocalTime(tmp.getDate()).toString());

						}
					}
				}
			};
		}
	}

	/*
	 * 
	 * teilt der Spalte mit, wie der Delete-und anzeigebuttns angezeigt werden
	 * sollen.
	 *
	 */
	private class ActionsColumnCallback implements
			Callback<TableColumn<de.htwsaar.pib.zms.server.model.Event, HBox>, TableCell<de.htwsaar.pib.zms.server.model.Event, HBox>> {

		public TableCell<de.htwsaar.pib.zms.server.model.Event, HBox> call(
				TableColumn<de.htwsaar.pib.zms.server.model.Event, HBox> column) {

			return new TableCell<de.htwsaar.pib.zms.server.model.Event, HBox>() {

				private JFXButton showEventBtn = new JFXButton("show");
				private JFXButton deleteEventBtn = new JFXButton("delete");
				private HBox buttonsContainer = new HBox();

				@Override
				protected void updateItem(HBox item, boolean empty) {
					super.updateItem(item, empty);
					TableRow<de.htwsaar.pib.zms.server.model.Event> tableRow = getTableRow();

					if (tableRow != null) {
						final de.htwsaar.pib.zms.server.model.Event tmp = tableRow.getItem();
						if (tmp != null) {
							addShowAction(tmp);
							addDeleteAction(tmp);
							buttonsContainer.setAlignment(Pos.CENTER);
							if (!buttonsContainer.getChildren().contains(showEventBtn)
									&& !buttonsContainer.getChildren().contains(deleteEventBtn)) {
								if (tmp.getEventCreator().getId() == user.getId())
									buttonsContainer.getChildren().addAll(showEventBtn, deleteEventBtn);
								else
									buttonsContainer.getChildren().add(showEventBtn);
								setGraphic(buttonsContainer);
							}

						}

					}

				}

				/*
				 * eines Termins aus eventsTable Loeschen.
				 */
				private void addDeleteAction(de.htwsaar.pib.zms.server.model.Event tmp) {
					deleteEventBtn.setOnAction(event -> {
						eventsTable.getSelectionModel().select(tmp);
						removeEvent(tmp);
					});
				}

				/*
				 * eines Termins aus eventsTable editieren.
				 */
				private void addShowAction(de.htwsaar.pib.zms.server.model.Event tmp) {
					showEventBtn.setOnAction(event -> {
						eventsTable.getSelectionModel().select(tmp);
						// int index = indexOf(tmp, eventsList);
						// eventsList.remove(index);
						updateEvent(tmp);
					});
				}

			};
		}

	}

	private int indexOf(de.htwsaar.pib.zms.server.model.Event event, List<de.htwsaar.pib.zms.server.model.Event> list) {
		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			if (event.equals(list.get(i))) {
				index = i;
				return index;
			}
		}
		return index;
	}

	private LocalDate convertToLocalDateViaInstant(java.util.Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private String convertToLocalTime(java.util.Date date) {
		SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
		String time = localDateFormat.format(date);
		return time;
	}

	void updateEvent(de.htwsaar.pib.zms.server.model.Event event) {
		UpdateEventController.setEvent(event);
		UpdateEventController.setAllEventsController(this);
		UpdateEventController.setUser(user);
		FXMLLoader fxmlLoader = new FXMLLoader();
		DialogPane dialogLoader;
		try {
			dialogLoader = fxmlLoader.load(getClass().getResource(Constants.UPDATE_EVENT_FXML).openStream());
			UpdateEventController updateEventController = (UpdateEventController) fxmlLoader.getController();
			updateEventController.setHomeController(homeController);
			updateEventController.setEventsTable(eventsTable);
			Dialog<Object> addDialog = createDialog(dialogLoader);
			addDialog.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	 * den Stil der Komponenten initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void setStyle() {
		allEventsRoot.getStylesheets().add(getClass().getResource(Constants.STYLES_CSS).toExternalForm());
		Image manWithPen = new Image(getClass().getResource("/img/events/manWithPen.png").toExternalForm());
		manWithPenImgView.setImage(manWithPen);
	}

}
