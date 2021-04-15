package de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import animatefx.animation.RubberBand;
import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.notificationcontroller.NotificationEntryController;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.notificationcontroller.NotificationsController;
import de.htwsaar.pib.vs.zms.client.controller.usercontroller.AllUsersController;
import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.serviceMessages.Change;
import de.htwsaar.pib.vs.zms.client.serviceMessages.Modelclass;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsEvent;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsObserver;
import de.htwsaar.pib.vs.zms.client.utils.ResizeHelper;
import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

public class MainViewController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private StackPane mainRoot, notificationsArrowContainer, settingsArrowContainer, settingsIconContainer,
			notificationsIconContainer, loggedInStatusIconContainer;
	@FXML
	private AnchorPane mainContainer, notificationPane, settingsPane, toolsPane, notificationsBtnContainer,
			notificationsContainer, clockContainer;
	@FXML
	private Label showAllLabel, roleField;
	@FXML
	private JFXComboBox<String> navigationComboBox;
	@FXML
	private JFXButton settingsButton, notificationButton, logOutBtn;
	@FXML
	private VBox notificationsScrollContainer;
	@FXML
	private JFXTextField usernameField;

	/****************************** Variablen ******************************/
	@Getter
	@Setter
	private static User user;
	private List<Notification> lastNotifications;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setStyle();
		setIcons();
		initializeClock();
		initializeSettingsPane();
		initializeNavigationComboBox();

		try {
			initializeNotifications();
			goToHome();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		subscribe();
	}

	public void handleEvent(ZmsEvent e) {
		FXMLLoader fxmlLoader = new FXMLLoader();

		if (e.getModelClass().equals(Modelclass.Notification)) {
			if (e.getChangetype().equals(Change.newObject)) {
				try {
					System.out.println("Sie haben eine neue Benachrichtigung");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void subscribe() {
		ZmsObserver observer = new MyZmsObserver(this);
		ServiceFacade.subscribeToServerEvents(observer);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void refreshView(ActionEvent event) {
		try {
			initializeNotifications();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initializeSettingsPane() {
		usernameField.setText(user.getUsername());
		if (user.getRole().equals("ROLE_USER"))
			roleField.setText("als Benutzer angemeldet");
		else if (user.getRole().equals("ROLE_ADMIN"))
			roleField.setText("als Admin angemeldet");
		else if (user.getRole().equals("ROLE_SUPER_USER"))
			roleField.setText("als Superuser angemeldet");
	}

	/**
	 * zu der in der {@link #navigationComboBox} ausgewaehlten Seite wechseln.
	 * 
	 * @param event
	 * @throws IOException
	 * 
	 * @author Feras Ejneid
	 */
	@FXML
	void navigate(ActionEvent event) throws IOException {
		String comboBoxValue = navigationComboBox.getValue();
		if (comboBoxValue.equalsIgnoreCase(Constants.EINSTELLUNGEN))
			goToSettings();
		else if (comboBoxValue.equalsIgnoreCase(Constants.HOME))
			goToHome();
		else if (comboBoxValue.equalsIgnoreCase(Constants.BENACHRICHTIGUNGEN))
			goToNotifications();
		else if (comboBoxValue.equalsIgnoreCase(Constants.USERS))
			goToUsers();
	}

	/**
	 * der Beschriftung {@link # showAllLabel} eine Unterstreichung hinzufugen, wenn
	 * die Maus darauf eingegeben wurde.
	 * 
	 * @param event
	 * @author Feras Ejneid
	 */
	@FXML
	void addLabelEffect(MouseEvent event) {
		showAllLabel.setUnderline(true);
		showAllLabel.setCursor(Cursor.HAND);
	}

	/**
	 * die Unterstreichung von der Beschriftung {@link # showAllLabel} loeschen,
	 * wenn die Maus sie verlassen hat.
	 * 
	 * @param event
	 * 
	 * @author Feras Ejneid
	 */
	@FXML
	void removeLabelEffect(MouseEvent event) {
		showAllLabel.setUnderline(false);
		showAllLabel.setCursor(Cursor.DEFAULT);
	}

	/**
	 * 
	 * zum Benachrichtigungsansicht navigieren, wenn das Label
	 * {@link # showAllLabel} geklickt wurde.
	 * 
	 * @param event
	 * @throws IOException
	 * 
	 * @author Feras Ejneid
	 */
	@FXML
	void goToNotifications(MouseEvent event) throws IOException {
		hideNotifications();
		navigationComboBox.setValue(Constants.BENACHRICHTIGUNGEN);
	}

	@FXML
	void goToSettings(ActionEvent event) throws IOException {
		hideSettings();
		navigationComboBox.setValue(Constants.EINSTELLUNGEN);
	}

	/**
	 * Benachrichtigungensicht im Hauptteil der Hauptseite einsetzen und andere
	 * Sichte loeschen, wenn sie vorhanden sind.
	 * 
	 * @throws IOException
	 * 
	 * @author Feras Ejneid
	 */
	private void goToNotifications() throws IOException {
		List<String> viewsToRemove = new ArrayList<>(Arrays.asList("homeRoot", "settingsRoot"));
		if (checkPrivileges(user))
			viewsToRemove.add("usersRoot");
		NotificationsController.setMainViewController(this);
		goTo(Constants.NOTIFICATIONS_FXML, viewsToRemove);
	}

	/**
	 * 
	 * Nutzeransicht im Hauptteil der Hauptseite einsetzen und andere Sichte
	 * loeschen, wenn sie vorhanden sind. Diese Option wird nur angezeigt, wenn ein
	 * Nutzer als ein Adminstrator oder ein Superuser angemeldet ist.
	 *
	 * @throws IOException
	 *
	 * @author Feras Ejneid
	 */
	private void goToUsers() throws IOException {
		List<String> viewsToRemove = new ArrayList<>(Arrays.asList("homeRoot", "settingsRoot", "notificationsRoot"));
		AllUsersController.setMainViewController(this);
		AllUsersController.setUser(user);
		goTo(Constants.USERS_FXML, viewsToRemove);
	}

	/**
	 * Einstellungensicht im Hauptteil der Hauptseite einsetzen und andere Sichte
	 * loeschen, wenn sie vorhanden sind.
	 * 
	 * @throws IOException
	 * 
	 * @author Feras Ejneid
	 */
	private void goToSettings() throws IOException {
		List<String> viewsToRemove = new ArrayList<>(Arrays.asList("homeRoot", "notificationsRoot", "settingsRoot"));
		if (checkPrivileges(user))
			viewsToRemove.add("usersRoot");
		goTo(Constants.SETTINGS_FXML, viewsToRemove);
	}

	/**
	 * Homesicht im Hauptteil der Hauptseite einsetzen und andere Sichte loeschen,
	 * wenn sie vorhanden sind.
	 * 
	 * @throws IOException
	 * 
	 * @author Feras Ejneid
	 */
	private void goToHome() throws IOException {
		List<String> viewsToRemove = new ArrayList<>(Arrays.asList("notificationsRoot", "settingsRoot"));
		if (checkPrivileges(user))
			viewsToRemove.add("usersRoot");
		HomeController.setUser(user);
		HomeController.setMainViewController(this);
		goTo(Constants.HOME_FXML, viewsToRemove);
	}

	/**
	 * zum angegebenen Zielseite wechseln nach dem Löschen der aktuell vorhandenen
	 * Komponenten im Hauptteil der Hauptseite.
	 * 
	 * @param target
	 * @param viewToRemove
	 * @throws IOException
	 * 
	 * @author Feras Ejneid
	 */
	private void goTo(String target, List<String> viewsToRemove) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader();
		Parent root = fxmlLoader.load(MainViewController.class.getResource(target).openStream());

		for (String view : viewsToRemove)
			mainContainer.getChildren().removeIf(c -> view.equals(c.getId()));

		AnchorPane.setBottomAnchor(root, 0.0);
		AnchorPane.setLeftAnchor(root, 0.0);
		AnchorPane.setRightAnchor(root, 0.0);
		AnchorPane.setTopAnchor(root, 0.0);
		mainContainer.getChildren().add(root);

		toolsPane.toFront();
		notificationPane.toFront();
		settingsPane.toFront();
	}

	/**
	 * Wenn der Einstellungsbereich {@link #settingsPane} nicht sichtbar ist, wird
	 * er angezeigt und der Inhalt der Einstellungsschaltflaeche wird in ein
	 * Schliesssymbol konvertiert, sodass der Benutzer weiß, dass er noch einmal
	 * darauf klicken muss, wenn er den Einstellungsbereich schliessen moechte.
	 *
	 * @param event
	 *
	 * @author Feras Ejneid
	 */
	@FXML
	void showSettings(ActionEvent event) {
		if (notificationPane.isVisible())
			hideNotifications();

		if (!settingsPane.isVisible())
			showSettings();

		else
			hideSettings();

	}

	/**
	 * Wenn der Benachrichtigungsberecih {@link #notificationPane} nicht sichtbar
	 * ist, wird er angezeigt und der Inhalt der
	 * Benachrichtigungsberecihschaltflaeche wird in ein Schliesssymbol konvertiert,
	 * sodass der Benutzer weiß, dass er noch einmal darauf klicken muss, wenn er
	 * den Benachrichtigungsberecih schliessen moechte.
	 *
	 * @param event
	 *
	 * @author Feras Ejneid
	 */
	@FXML
	void showNotifications(ActionEvent event) {
		if (settingsPane.isVisible())
			hideSettings();

		if (!notificationPane.isVisible())
			showNotifications();
		else
			hideNotifications();

		notificationsContainer.getChildren().removeIf(c -> c.getId().equals("notificationCircleContainer"));
		lastNotifications.stream().forEach(notification -> {
			notification.setSeen(true);
			try {
				ServiceFacade.setNotificationSeen(notification);
			} catch (NoConnectionToServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@FXML
	void logOut(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.LOGIN_FXML));
		Parent loginRoot = loader.load();
		displayInStage(loginRoot);
	}

	/**
	 * neue Buehne schaffen und den root auf ihr anzeigen.
	 * 
	 * @param root
	 * 
	 * @author Feras Ejneid
	 */
	private void displayInStage(Parent root) {
		Scene scene = new Scene(root, 930.0, 455.0);

		Stage mainStage = new Stage();
		mainStage.setScene(scene);
		mainStage.initStyle(StageStyle.TRANSPARENT);
		mainStage.setTitle(Constants.STAGE_TITLE);
		mainStage.setMinWidth(930.0);
		mainStage.setMinHeight(455.0);
		mainStage.setOnCloseRequest(event -> {
			Platform.exit();
			ClockController.stopClock();
		});
		Stage currentStage = (Stage) logOutBtn.getScene().getWindow();
		currentStage.hide();
		ResizeHelper.addResizeListener(mainStage, 930.0, 455.0, 930.0, 455.0);
		mainStage.show();

	}

	/**
	 * Benachrichtigungsberecih mit Animation anzeigen und der Inhalt der
	 * Benachrichtigungsberecihschaltflaeche in ein Schliesssymbol konvertieren,
	 * sodass der Benutzer weiß, dass er noch einmal darauf klicken muss, wenn er
	 * den Benachrichtigungsberecih schliessen moechte.
	 * 
	 * @author Feras Ejneid
	 */
	private void showNotifications() {
		notificationPane.setVisible(true);
		MaterialIconView closeNotificationsPaneIcon = new MaterialIconView(MaterialIcon.CLOSE);
		closeNotificationsPaneIcon.setSize(Constants.SIZE_2EM);
		notificationButton.setGraphic(closeNotificationsPaneIcon);
		RubberBand rubberBand = new RubberBand(notificationPane);
		rubberBand.play();
	}

	/**
	 * Benachrichtigungsberecih schliessen und der Inhalt der
	 * Benachrichtigungsberecihschaltflaeche in ein Benachrichtigungssymbol
	 * konvertieren.
	 * 
	 * @author Feras Ejneid
	 */
	public void hideNotifications() {
		notificationPane.setVisible(false);
		MaterialIconView notificationsIcon = new MaterialIconView(MaterialIcon.NOTIFICATIONS);
		notificationsIcon.setSize(Constants.SIZE_2EM);
		notificationButton.setGraphic(notificationsIcon);
	}

	/**
	 * Einstellungenbereich mit Animation anzeigen und der Inhalt der
	 * Einstellungenbereichschaltflaeche in ein Schliesssymbol konvertieren, sodass
	 * der Benutzer weiß, dass er noch einmal darauf klicken muss, wenn er den
	 * Einstellungenbereich schliessen moechte.
	 * 
	 * @author Feras Ejneid
	 */
	private void showSettings() {
		settingsPane.setVisible(true);
		MaterialIconView closeSettingsPaneIcon = new MaterialIconView(MaterialIcon.CLOSE);
		closeSettingsPaneIcon.setSize(Constants.SIZE_2EM);
		settingsButton.setGraphic(closeSettingsPaneIcon);
		RubberBand rubberBand = new RubberBand(settingsPane);
		rubberBand.play();
	}

	/**
	 * Einstellungenbereich schliessen und der Inhalt der
	 * Einstellungenbereichschaltflaeche in ein Einstellungensymbol konvertieren.
	 * 
	 * @author Feras Ejneid
	 */
	public void hideSettings() {
		FontAwesomeIconView settingsIcon = new FontAwesomeIconView(FontAwesomeIcon.GEAR);
		settingsIcon.setSize(Constants.SIZE_2EM);
		settingsButton.setGraphic(settingsIcon);
		settingsPane.setVisible(false);
	}

	/**
	 * Container des erstellten Benachrichtigungskreises animieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void addJumpAnimation(int number) {
		Node notificationsCircleContainer = createNotification(number);

		final TranslateTransition translateTransition = new TranslateTransition(Duration.millis(200),
				notificationsCircleContainer);
		final double start = 0.0;
		final double end = start - 4.0;
		translateTransition.setFromY(start);
		translateTransition.setToY(end);
		translateTransition.setCycleCount(-1);
		translateTransition.setAutoReverse(true);
		translateTransition.setInterpolator(Interpolator.EASE_BOTH);
		translateTransition.play();
	}

	/**
	 * ein Kreis mit der Anzahl der Benachrichtigungen ueber dem
	 * Benachrichtigungssymbol hinzufuegen.
	 * 
	 * @param number Anzahl der Benachrichtigungen
	 * @return Stackepane Container des erstellten Benachrichtigungskreises.
	 * 
	 * @author Feras Ejneid
	 */
	private Node createNotification(int number) {
		StackPane notificationsCircleContainer = new StackPane();
		notificationsCircleContainer.setId("notificationCircleContainer");
		Label lab = new Label(String.valueOf(number));
		lab.setStyle("-fx-font-family: \"SF UI Text Bold\"; -fx-text-fill:white;  -fx-font-size:11;  fx-padding:5;");
		Circle circle = new Circle(10, Color.rgb(250, 44, 86, .9));
		circle.setStrokeWidth(2.0);
		circle.setStyle("-fx-background-insets: 0 0 -1 0, 0, 1, 2;");
		circle.setSmooth(true);
		notificationsCircleContainer.getChildren().addAll(circle, lab);
		AnchorPane.setTopAnchor(notificationsCircleContainer, 5.0);
		notificationsContainer.getChildren().add(notificationsCircleContainer);
		return notificationsCircleContainer;
	}

	/**
	 * die Digitaluhr in die Hauptseite einsetzen.
	 * 
	 * @author Feras Ejneid
	 */
	private void initializeClock() {
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			Node clock = fxmlLoader.load(getClass().getResource(Constants.CLOCK_FXML).openStream());
			clockContainer.getChildren().add(clock);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@link #navigationComboBox} mit Fensternamen initialisieren. Wenn der
	 * angemeldete Nutzer ein Adminstrator oder ein Superuser ist, dann wird zu
	 * seinem {@link #navigationComboBox} Nutzerseite hinzugefuegt.
	 *
	 * @author Feras Ejneid
	 */
	private void initializeNavigationComboBox() {
		List<String> navigationLabelsList = new ArrayList<>(
				Arrays.asList(Constants.HOME, Constants.BENACHRICHTIGUNGEN, Constants.EINSTELLUNGEN));

		if (checkPrivileges(user))
			navigationLabelsList.add(2, Constants.USERS);

		navigationComboBox.getItems().addAll(navigationLabelsList);
		navigationComboBox.setValue(Constants.HOME);

		navigationComboBox.focusedProperty().addListener(n -> {
			hideNotifications();
			hideSettings();
		});
	}

	/**
	 * ueberprueft, ob der Nutzer ein Adminstrator, ein normaler Nutzer oder beides
	 * ist.
	 *
	 *
	 * @param user
	 * @return True, wenn der Nutzer Berechtigungen hat.
	 *
	 * @author Feras Ejneid
	 */
	private boolean checkPrivileges(User user) {
		return user.getRole().equals("ROLE_ADMIN") || user.getRole().equals("ROLE_SUPERUSER");
	}

	/**
	 * bestimmte Anzahl von Benachrichtigungen in den
	 * {@link #notificationsScrollContainer} eintragen.
	 * 
	 * Hier werden immer die letzten 9 Benachrichtigungen eingetragen.
	 * 
	 * @author Feras Ejneid
	 * @throws IOException
	 * @throws NoConnectionToServerException
	 */
	private void initializeNotifications() throws IOException, NoConnectionToServerException {

		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			lastNotifications = null;
			lastNotifications = ServiceFacade.findLastNotifications(9);
		} catch (NoConnectionToServerException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < lastNotifications.size(); i++) {
			Notification notification = lastNotifications.get(i);
			if (!notification.isSeen()) {
				addNotificationEntry(notification);
				addJumpAnimation(i + 1);
			}

		}
	}

	/**
	 * Einen Benachrichtigungseintrag in den {@link #notificationsContainer}
	 * eintragen.
	 * 
	 * @param notification
	 * @throws IOException
	 * @author Feras Ejneid
	 */
	private void addNotificationEntry(Notification notification) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.NOTIFICATION_ENTRY_FXML));
		Parent notificationEntry = loader.load();
		// Node notificationEntry = fxmlLoader
		// .load(getClass().getResource(Constants.NOTIFICATION_ENTRY_FXML).openStream());
		NotificationEntryController notificationEntryController = loader.getController();
		notificationEntryController.setNotificationText("Du hast eine Benachrichtigung von "
				+ notification.getSender().getUsername() + " : " + notification.getTitle());
		notificationEntryController.setNotification(notification);
		notificationsScrollContainer.getChildren().add(notificationEntry);
	}

	/**
	 * den Stil der Komponenten initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void setStyle() {
		mainRoot.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
		navigationComboBox.setStyle(Constants.FONT_BOLD_24);

		DropShadow shadow = new DropShadow();
		shadow.setBlurType(BlurType.GAUSSIAN);
		shadow.setRadius(7);
		shadow.setColor(Color.DARKGREY);

		settingsPane.setEffect(shadow);
		notificationPane.setEffect(shadow);
		// todaysEventsPane.setEffect(shadow);

	}

	/**
	 * Symbole hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void setIcons() {
		MaterialDesignIconView calendarIcon = new MaterialDesignIconView(MaterialDesignIcon.CALENDAR_TODAY);
		calendarIcon.setSize(Constants.SIZE_2EM);
		// calendarIconContainer.getChildren().add(calendarIcon);

		FontAwesomeIconView settingsIcon = new FontAwesomeIconView(FontAwesomeIcon.GEAR);
		settingsIcon.setSize(Constants.SIZE_2EM);
		settingsButton.setGraphic(settingsIcon);

		MaterialIconView notificationsIcon = new MaterialIconView(MaterialIcon.NOTIFICATIONS);
		notificationsIcon.setSize(Constants.SIZE_2EM);
		notificationButton.setGraphic(notificationsIcon);

		MaterialIconView settingsArrowIcon = new MaterialIconView(MaterialIcon.PLAY_ARROW);
		settingsArrowIcon.setRotate(27);
		settingsArrowIcon.setSize(Constants.SIZE_3EM);
		settingsArrowIcon.setGlyphStyle("-fx-fill: #fff;");

		settingsArrowContainer.getChildren().add(settingsArrowIcon);

		MaterialIconView notificationsArrowIcon = new MaterialIconView(MaterialIcon.PLAY_ARROW);
		notificationsArrowIcon.setRotate(27);
		notificationsArrowIcon.setSize(Constants.SIZE_3EM);
		notificationsArrowIcon.setGlyphStyle("-fx-fill: #fff;");
		notificationsArrowContainer.getChildren().add(notificationsArrowIcon);

		FontAwesomeIconView loggedInStatusIcon = new FontAwesomeIconView(FontAwesomeIcon.CIRCLE);
		loggedInStatusIcon.setSize(Constants.SIZE_1EM);
		loggedInStatusIcon.setGlyphStyle("-fx-fill: #57B846;");
		loggedInStatusIconContainer.getChildren().add(loggedInStatusIcon);
	}
}