package de.htwsaar.pib.vs.zms.client.controller.homecontroller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextArea;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.zms.server.model.Event;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Setter;

public class TodaysEventEntryController implements Initializable {

	@FXML
	private AnchorPane todaysEventEntryRoot;
	@FXML
	private HBox todayEventEntry;
	@FXML
	private JFXTextArea startTime;
	@FXML
	private StackPane eventStatusIconContainer;
	@FXML
	private TextArea eventTitle;
	@FXML
	private Label startEndTime;

	@Setter
	private static String startTimeValue, eventTitleValue, startEndTimeValue;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setEntryDetails();
		setIcons();
	}

	/**
	 * Symbole hinzufuegen.
	 * 
	 * @author Feras Ejneid
	 */
	private void setIcons() {
		// FontAwesomeIconView eventStatusIcon = new
		// FontAwesomeIconView(FontAwesomeIcon.CIRCLE);
		// eventStatusIcon.setSize("0.6em");
		// eventStatusIcon.setGlyphStyle("-fx-fill: #9D9D9D;");
		// eventStatusIconContainer.getChildren().add(eventStatusIcon);

		FontAwesomeIconView eventStatusIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE);
		eventStatusIcon.setSize(Constants.SIZE_1EM);
		eventStatusIcon.setGlyphStyle("-fx-fill: #57B846;");
		eventStatusIconContainer.getChildren().add(eventStatusIcon);
	}

	private void setEntryDetails() {
		startTime.setText(startTimeValue);
		eventTitle.setText(eventTitleValue);
		startEndTime.setText(startEndTimeValue);
	}
}
