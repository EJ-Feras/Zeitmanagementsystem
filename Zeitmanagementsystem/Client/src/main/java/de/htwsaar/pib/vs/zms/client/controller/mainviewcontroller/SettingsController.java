package de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.utils.Animation;
import de.htwsaar.pib.zms.server.model.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class SettingsController implements Initializable {

	/****************************** FX Attribute ******************************/
	@FXML
	private AnchorPane settingsRoot, userInfoPane, notificationsSettingsPane;
	@FXML
	private GridPane mainGrid;
	@FXML
	private StackPane userIconContainer, notificationsIconContainer;
	@FXML
	private ImageView manWithGearsImgView;
	@FXML
	private JFXTextField usernameField, firstNameField, secondNameField, emailField;
	@FXML
	private JFXPasswordField passwordField;

	/****************************** Variablen ******************************/
	private User user;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		user = MainViewController.getUser();
		setStyle();
		setIcons();
		animatePanes();
	}

	@FXML
	void saveUserInfo(ActionEvent event) {
		String newPassword = passwordField.getText();
		String newFirstName = firstNameField.getText();
		String newSecondName = secondNameField.getText();
		String newEmail = emailField.getText();

		user.setPassword(newPassword);
		if (!newFirstName.isEmpty() && !newFirstName.equalsIgnoreCase(user.getFirstName()))
			user.setFirstName(newFirstName);
		if (!newSecondName.isEmpty() && !newSecondName.equalsIgnoreCase(user.getSecondName()))
			user.setSecondName(newSecondName);
		if (!newEmail.isEmpty() && !newEmail.equalsIgnoreCase(user.getEmail()))
			user.setEmail(newEmail);

		try {
			ServiceFacade.saveUpdatedUser(user);
		} catch (NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * den Panes in der Einstellungsansicht Uebergaenge hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void animatePanes() {
		Animation.slideDown(userInfoPane, 20);
		Animation.slideDown(notificationsSettingsPane, 40);
	}

	/**
	 * den Stil der Komponenten initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void setStyle() {
		Image manWithGears = new Image(getClass().getResource("/img/settings/manWithGears.png").toExternalForm());
		manWithGearsImgView.setImage(manWithGears);

		DropShadow shadow = new DropShadow();
		shadow.setBlurType(BlurType.GAUSSIAN);
		shadow.setRadius(7);
		shadow.setColor(Color.DARKGREY);

		userInfoPane.setEffect(shadow);
		notificationsSettingsPane.setEffect(shadow);
	}

	/**
	 * Symbole hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void setIcons() {
		MaterialIconView notificationsIcon = new MaterialIconView(MaterialIcon.NOTIFICATIONS);
		notificationsIcon.setSize(Constants.SIZE_2EM);
		notificationsIconContainer.getChildren().add(notificationsIcon);

		FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
		userIcon.setSize(Constants.SIZE_2EM);
		userIconContainer.getChildren().add(userIcon);
	}
}