package de.htwsaar.pib.vs.zms.client.controller.eventcontroller;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXCheckBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.Setter;

public class InvitableUserEntryController implements Initializable {

	@FXML
	private AnchorPane invitableUserEntry;
	@FXML
	private Label userDetails, userStatus;;
	@FXML
	private JFXCheckBox checkBox;

	@Setter
	private static String details, status;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		initializeUserDetais();
	}

	private void initializeUserDetais() {
		userDetails.setText(details);
		userStatus.setText(status);
	}

	public void deleteCheckBox() {
		invitableUserEntry.getChildren().remove(2);
	}

	@FXML
	void check(ActionEvent event) {

	}

	public void setCheckBox() {
		checkBox.setSelected(true);
		checkBox.setDisable(true);
	}

}
