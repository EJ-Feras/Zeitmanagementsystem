package de.htwsaar.pib.vs.zms.client;

import de.htwsaar.pib.vs.zms.client.controller.Constants;
import de.htwsaar.pib.vs.zms.client.utils.ResizeHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CalendarClientStarter extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.LOGIN_FXML));
		Parent loginRoot = loader.load();
		Scene scene = new Scene(loginRoot, 980, 620);

		primaryStage.initStyle(StageStyle.TRANSPARENT);
		scene.setFill(Color.TRANSPARENT);
		primaryStage.setTitle(Constants.STAGE_TITLE);
		primaryStage.setWidth(930.0);
		primaryStage.setHeight(455.0);
		primaryStage.setScene(scene);
		// alle laufenden Threads stoppen
		primaryStage.setOnCloseRequest(event -> {
			Platform.exit();
		});
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setAlwaysOnTop(false);
		primaryStage.show();
		ResizeHelper.addResizeListener(primaryStage, 930.0, 455.0, 930.0, 455.0);

	}

}
