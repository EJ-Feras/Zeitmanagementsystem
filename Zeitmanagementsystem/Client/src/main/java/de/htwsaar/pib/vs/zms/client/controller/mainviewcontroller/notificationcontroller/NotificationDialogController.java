package de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller.notificationcontroller;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.exceptions.NoConnectionToServerException;
import de.htwsaar.pib.vs.zms.client.service.ServiceFacade;
import de.htwsaar.pib.vs.zms.client.utils.WindowManager;
import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.User;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.Setter;

public class NotificationDialogController implements Initializable{

    @FXML
    private DialogPane updateEventRoot;
    @FXML
    private AnchorPane datumField;
    @FXML
    private JFXTextField titleField;
    @FXML
    private JFXTextField duration, senderField;
    @FXML
    private JFXTextArea notes;
    @FXML
    private JFXTextField dateField;
    @FXML
    private JFXTextField senderId;
    @FXML
    private JFXTextField eventId;
    @FXML
    private StackPane closeIconContainer;
    @FXML
    private JFXButton closeButton;
    @FXML
    private JFXButton acceptBtn;
    @FXML
    private JFXButton declineBtn;
    @Setter
    private static Notification notification;
    @Setter
    private static NotificationsController notificationsController;
    @Setter
    private static boolean hideAcceptAndDecline;
    

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setIcons();
		initializeFields();
	}
    
	
	private void initializeFields(){
		titleField.setText(notification.getTitle());
		notes.setText(notification.getDescription());
		dateField.setText(notification.getEvent().getDate().toString());
		duration.setText(String.valueOf(notification.getEvent().getDuration()));
		senderField.setText(notification.getSender().getFirstName() + " " + notification.getSender().getSecondName());
		if(hideAcceptAndDecline){
			acceptBtn.setVisible(false);
			declineBtn.setVisible(false);
		}
	}
    @FXML
    void close(ActionEvent event) {
    	WindowManager.exit(closeButton);
    }
    
    @FXML
    void declineNotification(ActionEvent event) {
		try {
			ServiceFacade.declineInvitation(notification);
		} catch (NoConnectionToServerException e) {
			e.printStackTrace();
		}
    }

    @FXML
    void acceptInvitation(ActionEvent event) {
    	try {
			ServiceFacade.acceptInvitation(notification);
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
		MaterialIconView closeIconContainer = new MaterialIconView(MaterialIcon.CLOSE);
		closeIconContainer.setSize(Constants.SIZE_2EM);
		closeButton.setGraphic(closeIconContainer);
	}

}
