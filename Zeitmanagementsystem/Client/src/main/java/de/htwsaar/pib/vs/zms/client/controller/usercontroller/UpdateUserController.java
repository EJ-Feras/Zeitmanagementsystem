package de.htwsaar.pib.vs.zms.client.controller.usercontroller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.eventcontroller.AllEventsController;
import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.utils.WindowManager;
import de.htwsaar.pib.zms.server.model.User;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import lombok.Setter;

public class UpdateUserController implements Initializable {
	@FXML
	private DialogPane updateUserRoot;
	@FXML
	private JFXTextField usernameField, firstNameField, secondNameField, emailField;
	@FXML
	private JFXPasswordField passwordField;
	@FXML
	private JFXComboBox<String> roles;
	@FXML
	private StackPane closeIconContainer;
	@FXML
	private JFXButton closeButton;
	@FXML
	private ImageView lowerLeftCornerWaveImgView, upperRightCornerWaveImgView;

	@Setter
	private static User user;
	@Setter
	private List<User> users;
	@Setter
	private static AllUsersController allUsersController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setIcons();
		initializeRoles();
		initializeFields();
	}

	@FXML
	void close(ActionEvent event) {
		WindowManager.exit(closeButton);
	}

	private void initializeFields() {

		usernameField.setText(user.getUsername());
		firstNameField.setText(user.getFirstName());
		secondNameField.setText(user.getSecondName());
		emailField.setText(user.getEmail());

		if (user.getRole().equals("ROLE_USER"))
			roles.setValue("Benutzer");
		else if (user.getRole().equals("ROLE_ADMIN"))
			roles.setValue("Adminstrator");
		else if (user.getRole().equals("ROLE_SUPERUSER"))
			roles.setValue("Superuser");

	}

	private void initializeRoles() {
		roles.getItems().add("Benutzer");
		roles.getItems().add("Adminstrator");
		roles.getItems().add("Superuser");
	}

	@FXML
	void saveUser(ActionEvent event) {
		String username = usernameField.getText().toLowerCase().trim();
		String password = passwordField.getText().trim();
		String firstName = firstNameField.getText().toLowerCase().trim();
		String secondName = secondNameField.getText().toLowerCase().trim();
		String email = emailField.getText().toLowerCase().trim();
		String role = roles.getValue();
		if (role.equalsIgnoreCase("user"))
			role = "ROLE_USER";
		else if (role.equalsIgnoreCase("adminstrator"))
			role = "ROLE_ADMIN";
		else if (role.equalsIgnoreCase("superuser"))
			role = "ROLE_SUPERUSER";

		if (password.isEmpty())
			user.setPassword(null);
		else
			user.setPassword(passwordField.getText());

		user.setUsername(username);
		user.setFirstName(firstName);
		user.setSecondName(secondName);
		user.setEmail(email);

		try {
			ServiceFacade.saveUpdatedUser(user);

			users = ServiceFacade.findAllUsers();
			AllUsersController.setUsers(users);
			allUsersController.refreshUsersTable();
		} catch (NoConnectionToServerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Symbole hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void setIcons() {
		Image lowerLeftCornerWave = new Image(
				getClass().getResource("/img/user/lowerLeftCornerWave.png").toExternalForm());
		lowerLeftCornerWaveImgView.setImage(lowerLeftCornerWave);

		Image upperRightCornerWave = new Image(
				getClass().getResource("/img/user/upperRightCornerWave.png").toExternalForm());
		upperRightCornerWaveImgView.setImage(upperRightCornerWave);

		MaterialIconView closeIconContainer = new MaterialIconView(MaterialIcon.CLOSE);
		closeIconContainer.setSize(Constants.SIZE_2EM);
		closeButton.setGraphic(closeIconContainer);
	}
}