package de.htwsaar.pib.vs.zms.client.controller.eventcontroller;

import java.net.URL;
import java.util.ResourceBundle;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;

import javafx.scene.layout.AnchorPane;
import lombok.Setter;

public class CalendarEventCellController implements Initializable {

	/****************************** FX-Attribute ******************************/
	@FXML
	private AnchorPane cellRoot;
	@FXML
	private TextArea eventTitle;
	@Setter
	private static String title;

	/****************************** Methoden ******************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setStyle();
		eventTitle.setTooltip(new Tooltip(title));
		eventTitle.setText(title);
	}

	/**
	 * den Stil der Komponenten initialisieren.
	 * 
	 * @author Feras Ejneid
	 */
	private void setStyle() {
		cellRoot.getStylesheets().add(getClass().getResource(Constants.STYLES_CSS).toExternalForm());
	}

	
}
