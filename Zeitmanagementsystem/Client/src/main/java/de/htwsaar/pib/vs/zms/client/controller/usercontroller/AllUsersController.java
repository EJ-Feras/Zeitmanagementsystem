package de.htwsaar.pib.vs.zms.client.controller.usercontroller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.DeleteController.DeletionController;
import de.htwsaar.pib.vs.zms.client.controller.eventcontroller.AllEventsController;
import de.htwsaar.pib.vs.zms.client.controller.eventcontroller.NewEventController;
import de.htwsaar.pib.vs.zms.client.controller.eventcontroller.UpdateEventController;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.MainViewController;
import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.exceptions.UnknownObjectException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.serviceMessages.Change;
import de.htwsaar.pib.vs.zms.client.serviceMessages.Modelclass;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsEvent;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsObserver;
import de.htwsaar.pib.vs.zms.client.utils.Animation;
import de.htwsaar.pib.vs.zms.client.utils.ResizeHelper;
import de.htwsaar.pib.zms.server.model.Notification;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;

public class AllUsersController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private AnchorPane usersRoot, usersTableContainer;
	@FXML
	private ImageView usersImgView;
	@FXML
	private TableView<User> usersTable;
	@FXML
	private TableColumn<User, String> usernameCol;
	@FXML
	private TableColumn<User, String> firstNameCol;
	@FXML
	private TableColumn<User, String> secondNameCol;
	@FXML
	private TableColumn<User, HBox> actionsCol;

	/****************************** Variablen ******************************/
	@Setter
	private static MainViewController mainViewController;
	@Getter
	@Setter
	private static User user;
	@Setter
	private static List<User> users;
	@Setter
	private static AllUsersController allUsersController;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setStyle();
		animateUsersTableContainer();
		initializeUsersTable();
		
	}

	
	

	@FXML
	void createNewUser(ActionEvent event) {
		NewUserController.setAllUsersController(this);
		if (user.getRole().equals("ROLE_ADMIN")) {
			FXMLLoader fxmlLoader = new FXMLLoader();
			DialogPane dialogLoader;
			try {

				dialogLoader = fxmlLoader.load(getClass().getResource(Constants.NEW_USER_ADMIN_FXML).openStream());
				NewUserController newUserController = (NewUserController) fxmlLoader.getController();

				Dialog<Object> addDialog = createDialog(dialogLoader);
				addDialog.showAndWait();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (user.getRole().equals("ROLE_SUPERUSER")) {
			FXMLLoader fxmlLoader = new FXMLLoader();
			DialogPane dialogLoader;
			try {
				dialogLoader = fxmlLoader.load(getClass().getResource(Constants.NEW_USER_SUPERUSER_FXML).openStream());
				NewUserController newUserController = (NewUserController) fxmlLoader.getController();

				Dialog<Object> addDialog = createDialog(dialogLoader);
				addDialog.showAndWait();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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

	void removeUser(User user) {
		FXMLLoader dialogLoader = new FXMLLoader();
		DialogPane confirmDeletion;
		try {
			confirmDeletion = dialogLoader.load(getClass().getResource(Constants.CONFIRM_DELETION_FXML).openStream());
			DeletionController deletionController = (DeletionController) dialogLoader.getController();
			List<de.htwsaar.pib.zms.server.model.Event> upcommingEvents = user.getUpcomingEvents();

			int indexOfUser = -1;
			for (int i = 0; i < upcommingEvents.size(); i++) {
				de.htwsaar.pib.zms.server.model.Event event = upcommingEvents.get(i);
				event.getParticipants().remove(user);
				try {
					ServiceFacade.saveChangesInEvent(event);
				} catch (NoConnectionToServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			user.setUpcomingEvents(null);

			deletionController.setUser(user);

			Dialog<Object> removeDialog = createDialog(confirmDeletion);
			abilityToCloseFromXButton(removeDialog);
			removeDialog.showAndWait();

			refreshUsersTable();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	 * der {@link #usersTableContainer} ein Uebergang hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void animateUsersTableContainer() {
		Animation.slideDown(usersTableContainer, 20);
	}

	/**
	 * die Tabelle aller Benutzer initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void initializeUsersTable() {
		try {
			users = ServiceFacade.findAllUsers();
		} catch (NoConnectionToServerException e) {
			e.printStackTrace();
		}
		usersTable.setItems(FXCollections.observableList(users));

		usernameCol.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
		firstNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
		secondNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("secondName"));
		actionsCol.setCellFactory(column -> new ActionsColumnCallback().call(column));

	}

	/*
	 * 
	 * teilt der Spalte mit, wie der Delete-und anzeigebuttons angezeigt werden
	 * sollen.
	 *
	 */
	private class ActionsColumnCallback implements Callback<TableColumn<User, HBox>, TableCell<User, HBox>> {

		public TableCell<User, HBox> call(TableColumn<User, HBox> column) {

			return new TableCell<User, HBox>() {

				private JFXButton showUserBtn = new JFXButton("show");
				private JFXButton deleteUserBtn = new JFXButton("delete");
				private HBox buttonsContainer = new HBox();

				@Override
				protected void updateItem(HBox item, boolean empty) {
					super.updateItem(item, empty);
					TableRow<User> tableRow = getTableRow();

					if (tableRow != null) {
						final User tmp = tableRow.getItem();
						if (tmp != null) {
							addShowAction(tmp);
							addDeleteAction(tmp);
							buttonsContainer.setAlignment(Pos.CENTER);
							if (!buttonsContainer.getChildren().contains(showUserBtn)
									&& !buttonsContainer.getChildren().contains(deleteUserBtn)) {
								buttonsContainer.getChildren().addAll(showUserBtn, deleteUserBtn);
								setGraphic(buttonsContainer);
							}

						}

					}

				}

				/*
				 * einen Benutzer aus usersTable loeschen.
				 */
				private void addDeleteAction(User tmp) {
					deleteUserBtn.setOnAction(event -> {
						usersTable.getSelectionModel().select(tmp);
						removeUser(tmp);

					});

				}

				/*
				 * eines Termins aus eventsTable editieren.
				 */
				private void addShowAction(User tmp) {
					showUserBtn.setOnAction(event -> {
						usersTable.getSelectionModel().select(tmp);
						// int index = indexOf(tmp, eventsList);
						// eventsList.remove(index);
						updateUser(tmp);
						// initializeEventsTable();
					});
				}

			};
		}

	}

	private int indexOf(User user, List<User> list) {
		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			if (user.equals(list.get(i))) {
				index = i;
				return index;
			}
		}
		return index;
	}

	private void updateUser(User user) {
		try {
			UpdateUserController.setUser(user);
			UpdateUserController.setAllUsersController(this);
			FXMLLoader fxmlLoader = new FXMLLoader();
			DialogPane dialogLoader;
			dialogLoader = fxmlLoader.load(getClass().getResource(Constants.UPDATE_USER_FXML).openStream());
			UpdateUserController updateUserController = (UpdateUserController) fxmlLoader.getController();

			Dialog<Object> addDialog = createDialog(dialogLoader);
			addDialog.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * den Stil der Komponenten initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void setStyle() {
		usersRoot.getStylesheets().add(getClass().getResource(Constants.STYLES_CSS).toExternalForm());
	}

	public void refreshUsersTable() {
		usersTable.getItems().clear();
		initializeUsersTable();
	}
}
