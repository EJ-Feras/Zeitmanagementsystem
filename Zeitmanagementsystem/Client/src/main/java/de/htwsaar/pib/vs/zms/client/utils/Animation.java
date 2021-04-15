package de.htwsaar.pib.vs.zms.client.utils;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animation {

	/**
	 * Nachuntenschiebenubergang fuer eine Komponente.
	 * 
	 * @param target der Komponent, auf die den Uebergang angwendet wird.
	 * @param yToUp  Startpunkt, ab der eine Komponente nach unten geschoben wird.
	 * 
	 * @author Feras Ejneid
	 */
	public static void slideDown(Node target, double yToUp) {
		double y = target.getLayoutY();

		TranslateTransition up = new TranslateTransition(Duration.seconds(0), target);

		up.toYProperty().set(-yToUp);

		TranslateTransition down = new TranslateTransition(Duration.seconds(1), target);
		down.toYProperty().set(y);

		SequentialTransition seq = new SequentialTransition(up, down);
		seq.play();
	}
}
