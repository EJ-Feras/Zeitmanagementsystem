package de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.notificationcontroller;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.MainViewController;
import de.htwsaar.pib.vs.zms.client.controller.usercontroller.UpdateUserController;
import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.utils.Animation;
import de.htwsaar.pib.vs.zms.client.utils.ResizeHelper;
import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.Notificationtype;
import de.htwsaar.pib.zms.server.model.User;
import javafx.collections.FXCollections;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;

public class NotificationsController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private AnchorPane notificationsRoot, notificationsTableContainer;
	@FXML
	private ImageView megaphoneImgView;
	@FXML
	private TableView<Notification> notificationsTable;
	@FXML
	private TableColumn<Notification, String> titleCol;
	@FXML
	private TableColumn<Notification, String> typeCol;
	@FXML
	private TableColumn<Notification, Date> dateCol;
	@FXML
	private TableColumn<Notification, HBox> actionsCol;

	/****************************** Variablen ******************************/
	@Setter
	private static MainViewController mainViewController;
	@Getter
	@Setter
	private static User user;
	private List<Notification> notificationsList;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setStyle();
		animateNotificationsTableContainer();
		initializeEventsTable();
	}

	/**
	 * der {@link #notificationsTableContainer} ein Uebergang hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void animateNotificationsTableContainer() {
		Animation.slideDown(notificationsTableContainer, 20);
	}

	/**
	 * die Tabelle aller Benachrichtigungen initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void initializeEventsTable() {
		try {
			notificationsList = ServiceFacade.findLastNotifications(100);
		} catch (NoConnectionToServerException e) {
			e.printStackTrace();
		}
		notificationsTable.setItems(FXCollections.observableList(notificationsList));

		titleCol.setCellValueFactory(new PropertyValueFactory<Notification, String>("title"));
		typeCol.setCellValueFactory(new PropertyValueFactory<Notification, String>("notificationtype"));
		dateCol.setCellValueFactory(new PropertyValueFactory<Notification, Date>("sendDate"));
		actionsCol.setCellFactory(column -> new ActionsColumnCallback().call(column));
	}

	/*
	 * 
	 * teilt der Spalte mit, wie der Delete-und anzeigebuttons angezeigt werden
	 * sollen.
	 *
	 */
	private class ActionsColumnCallback
			implements Callback<TableColumn<Notification, HBox>, TableCell<Notification, HBox>> {

		public TableCell<Notification, HBox> call(TableColumn<Notification, HBox> column) {

			return new TableCell<Notification, HBox>() {

				private JFXButton showNotificationBtn = new JFXButton("show");
				private JFXButton deleteNotificationBtn = new JFXButton("delete");
				private HBox buttonsContainer = new HBox();

				@Override
				protected void updateItem(HBox item, boolean empty) {
					super.updateItem(item, empty);
					TableRow<Notification> tableRow = getTableRow();

					if (tableRow != null) {
						final Notification tmp = tableRow.getItem();
						if (tmp != null) {
							addShowAction(tmp);
							addDeleteAction(tmp);
							buttonsContainer.setAlignment(Pos.CENTER);
							if (!buttonsContainer.getChildren().contains(showNotificationBtn)
									&& !buttonsContainer.getChildren().contains(deleteNotificationBtn)) {
								buttonsContainer.getChildren().addAll(showNotificationBtn, deleteNotificationBtn);
								setGraphic(buttonsContainer);
							}

						}

					}

				}

				/*
				 * eine Benachrichtigung aus notificationsTable Loeschen.
				 */
				private void addDeleteAction(Notification tmp) {
					deleteNotificationBtn.setOnAction(event -> {
						notificationsTable.getSelectionModel().select(tmp);
						int index = indexOf(tmp, notificationsList);
						notificationsList.remove(index);
						initializeEventsTable();
					});

				}

				private void addShowAction(Notification tmp) {
					showNotificationBtn.setOnAction(event -> {
						notificationsTable.getSelectionModel().select(tmp);

						showNotifi(tmp);
						// initializeEventsTable();
					});
				}

			};
		}

	}

	void showNotifi(Notification notifi) {
		try {
			NotificationDialogController.setNotification(notifi);
			NotificationDialogController.setNotificationsController(this);
			if(!notifi.getNotificationtype().equals(Notificationtype.invitation))
				NotificationDialogController.setHideAcceptAndDecline(true);
			FXMLLoader fxmlLoader = new FXMLLoader();
			DialogPane dialogLoader;
			dialogLoader = fxmlLoader.load(getClass().getResource(Constants.NOTIFICATION_DIALOG_FXML).openStream());

			Dialog<Object> addDialog = createDialog(dialogLoader);
			addDialog.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

	private int indexOf(Notification notification, List<Notification> list) {
		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			if (notification.equals(list.get(i))) {
				index = i;
				return index;
			}
		}
		return index;
	}

	/**
	 * den Stil der Komponenten initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void setStyle() {
		notificationsRoot.getStylesheets().add(getClass().getResource(Constants.STYLES_CSS).toExternalForm());
		Image megaphone = new Image(getClass().getResource("/img/notifications/megaphone.png").toExternalForm());
		megaphoneImgView.setImage(megaphone);
	}

}
