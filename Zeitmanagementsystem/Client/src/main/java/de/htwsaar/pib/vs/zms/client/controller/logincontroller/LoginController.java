package de.htwsaar.pib.vs.zms.client.controller.logincontroller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.ClockController;
import de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.MainViewController;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsEvent;
import de.htwsaar.pib.vs.zms.client.serviceMessages.ZmsObserver;
import de.htwsaar.pib.vs.zms.client.utils.WindowManager;

import de.htwsaar.pib.zms.server.model.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private AnchorPane login;
	@FXML
	private JFXTextField usernameInput;
	@FXML
	private JFXPasswordField passwordInput;
	@FXML
	private JFXButton signInBtn, closeButton;
	@FXML
	private StackPane usernameIconContainer, passwordIconContainer, closeIconContainer;
	@FXML
	private ImageView rocketImgView;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setIcons();
	}

	/**
	 * Uebergang des Raketenstarts in der Login-Ansicht.
	 * 
	 * @param event
	 * 
	 * @author Feras Ejneid
	 */
	@FXML
	void launchRocket(MouseEvent event) {
		double y = rocketImgView.getY();

		TranslateTransition up = new TranslateTransition(Duration.seconds(2), rocketImgView);
		up.toYProperty().set(-event.getSceneY() - rocketImgView.getFitHeight());

		TranslateTransition down = new TranslateTransition(Duration.seconds(2), rocketImgView);
		down.toYProperty().set(y);

		SequentialTransition seq = new SequentialTransition(up, down);
		seq.play();
	}

	/**
	 * zur Hauptseite nach der Anmeldung wechseln.
	 * 
	 * @param event
	 * @author Feras Ejneid
	 * @throws Exception
	 */
	@FXML
	void signIn(ActionEvent event) throws Exception {
		User user = ServiceFacade.logIn(usernameInput.getText().trim(), passwordInput.getText().trim());
		goToMainView(user);
	}
	
	/**
	 * zur Hauptseite wechseln.
	 *
	 *
	 * @param user
	 * @throws IOException
	 *
	 * @author Feras Ejneid
	 */
	private void goToMainView(User user) throws IOException {
		MainViewController.setUser(user);
		FXMLLoader fxmlLoader = new FXMLLoader();
		Parent root = fxmlLoader.load(LoginController.class.getResource(Constants.MAIN_VIEW).openStream());
		displayInStage(root);
	}

	/**
	 * neue Buehne schaffen und den root auf ihr anzeigen.
	 * 
	 * @param root
	 * 
	 * @author Feras Ejneid
	 */
	private void displayInStage(Parent root) {
		Scene scene = new Scene(root);

		Stage mainStage = new Stage();
		mainStage.setScene(scene);
		mainStage.setTitle(Constants.STAGE_TITLE);
		mainStage.setMinWidth(1290.0);
		mainStage.setMinHeight(800.0);
		mainStage.setScene(scene);
		mainStage.setOnCloseRequest(event -> {
			Platform.exit();
			ClockController.stopClock();
		});
		Stage currentStage = (Stage) signInBtn.getScene().getWindow();
		currentStage.hide();
		mainStage.show();
	}

	/**
	 * die Buhne shliessen.
	 * 
	 * @param event
	 * @author Feras Ejneid
	 */
	@FXML
	void close(ActionEvent event) {
		WindowManager.exit(closeButton);
	}

	/**
	 * Symbole hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void setIcons() {
		FontAwesomeIconView usernameIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
		usernameIcon.setSize(Constants.SIZE_2EM);
		usernameIcon.setGlyphStyle("-fx-fill: #F4F4F4;");
		usernameIconContainer.getChildren().add(usernameIcon);

		FontAwesomeIconView passwordIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
		passwordIcon.setSize(Constants.SIZE_2EM);
		passwordIcon.setGlyphStyle("-fx-fill: #F4F4F4;");
		passwordIconContainer.getChildren().add(passwordIcon);

		MaterialIconView closeIcon = new MaterialIconView(MaterialIcon.CLOSE);
		closeIcon.setSize(Constants.SIZE_2EM);
		closeIcon.setGlyphStyle("-fx-fill: #F4F4F4;");
		closeButton.setGraphic(closeIcon);
	}
}
