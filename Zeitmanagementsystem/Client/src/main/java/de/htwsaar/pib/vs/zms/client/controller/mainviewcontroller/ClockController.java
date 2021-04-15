package de.htwsaar.pib.vs.zms.client.controller.mainviewcontroller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import java.util.Calendar;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import java.util.GregorianCalendar;
import java.util.Locale;

import java.text.SimpleDateFormat;

/**
 * 
 * Hier wird die Digitaluhr gesteuert.
 * 
 * @author Feras Ejneid
 *
 */
public class ClockController implements Initializable, Runnable {

	/****************************** FX Attribute ******************************/
	@FXML
	private Text time;
	@FXML
	private Text period;

	/****************************** Variablen ******************************/
	private Calendar systemTime;
	private SimpleDateFormat sdfTime;
	private SimpleDateFormat sdfPeriod;
	private static Thread clockThread;
	private static boolean stop;

	/****************************** Methoden ******************************/

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		time.setText(formatTextTime());
		period.setText(formatTextPeriod());
		clockThread.start();
	}

	public ClockController() {
		sdfTime = new SimpleDateFormat("hh:mm");
		sdfPeriod = new SimpleDateFormat("a", Locale.ENGLISH);
		stop = false;
		clockThread = new Thread(this, "clock");
	}

	@Override
	public void run() {
		while (Thread.currentThread() == clockThread && !stop) {
			try {
				Thread.sleep(1000);
				time.setText(formatTextTime());
				period.setText(formatTextPeriod());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void stopClock() {
		stop = true;
		clockThread = null;
	}

	public String formatTextTime() {
		systemTime = new GregorianCalendar();
		return sdfTime.format(systemTime.getTime());
	}

	public String formatTextPeriod() {
		return sdfPeriod.format(systemTime.getTime());
	}
}