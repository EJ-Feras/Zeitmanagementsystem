package de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.notificationcontroller;

import java.net.URL;
import java.util.ResourceBundle;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.zms.server.model.Notification;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import lombok.Setter;

public class NotificationEntryController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private StackPane eyeIconContainer;
	@FXML
	private StackPane checkMarkIconContainer;
	@FXML
	private StackPane rejectIconContainer;
	@FXML
	private StackPane trashIconContainer;
	@FXML
	private TextArea notificationText;
	@FXML
	private Label elapsedTime;

	private Notification notification;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setIcons();
	}

	@FXML
	void show(MouseEvent event) {

	}

	@FXML
	void accept(MouseEvent event) {

	}

	@FXML
	void reject(MouseEvent event) {

	}

	@FXML
	void delete(MouseEvent event) {

	}

	public void setNotificationText(String text) {
		notificationText.setText(text);
	}

	public void setElapsedTime(String text) {
		elapsedTime.setText(text);
	}

	public void setNotification(Notification notification) {
		this.notification = notification;

	}

	/**
	 * Symbole hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void setIcons() {
//		MaterialIconView eyeIcon = new MaterialIconView(MaterialIcon.REMOVE_RED_EYE);
//		eyeIcon.setSize(Constants.SIZE_1_5EM);
//		eyeIconContainer.getChildren().add(eyeIcon);
//
//		MaterialIconView checkMarkIcon = new MaterialIconView(MaterialIcon.CHECK);
//		checkMarkIcon.setSize(Constants.SIZE_1_5EM);
//		checkMarkIconContainer.getChildren().add(checkMarkIcon);
//
//		MaterialIconView rejectIcon = new MaterialIconView(MaterialIcon.CLOSE);
//		rejectIcon.setSize(Constants.SIZE_1_5EM);
//		rejectIconContainer.getChildren().add(rejectIcon);
//
//		MaterialIconView trashIcon = new MaterialIconView(MaterialIcon.DELETE);
//		trashIcon.setSize(Constants.SIZE_1_5EM);
//		trashIconContainer.getChildren().add(trashIcon);
	}

}
