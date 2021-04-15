package de.htwsaar.pib.vs.zms.client.utils;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateTime {

	private static final String DEZ = "Dez";
	private static final String NOV = "Nov";
	private static final String OKT = "Okt";
	private static final String SEP = "Sep";
	private static final String AUG = "Aug";
	private static final String JUL = "Jul";
	private static final String JUN = "Jun";
	private static final String MAI = "Mai";
	private static final String APR = "Apr";
	private static final String MÄR = "Mär";
	private static final String FEB = "Feb";
	private static final String JAN = "Jan";
	private static Calendar calendar;

	/**
	 * ein Kalenderinstanz zuruckgeben.
	 * 
	 * @return Calendar
	 * 
	 * @author Feras Ejneid
	 */
	public static Calendar getCalearndarInstance() {
		calendar = Calendar.getInstance();
		return calendar;
	}

	/**
	 * Gibt die Anzahl der Tage fuer einen bestimmten Monat in einem bestimmten Jahr
	 * zuruek.
	 * 
	 * @param year
	 * @param month
	 * @return int Anzahl der Tage
	 * 
	 * @author Feras Ejneid
	 * 
	 */
	public static int getNumberOfDays(int year, int month) {
		GregorianCalendar calender = new GregorianCalendar(year, month, 1);
		int daysInMonth = calender.getActualMaximum(Calendar.DAY_OF_MONTH);
		return daysInMonth;
	}

	/**
	 * Gibt die Nummer des ersten Tages vom uebergebenen Monat zurueck z.B: Sonntag
	 * hat die Nummer 1, Montag hat die Nummer 2 etc.
	 * 
	 * 
	 * @param month
	 * @param year
	 * @return int Nummer der ersten Tag
	 * 
	 * @author Feras Ejneid
	 */

	public static int getFirstDayOfMonth(int month, int year) {
		Calendar calender = getCalearndarInstance();
		calender.set(Calendar.MONTH, month);
		calender.set(Calendar.YEAR, year);
		calender.set(Calendar.DAY_OF_MONTH, 1);

		return calender.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Gibt den Namen des vorherigen Monats zurueck.
	 * 
	 * @param year
	 * @param month
	 * @return String Name des vorherigen Monats
	 * 
	 * @author Feras Ejneid
	 */
	public static String getNameOfPrevtMonth(int year, int month) {
		LocalDate currentDate = LocalDate.of(year, month, 1);
		currentDate = currentDate.minusMonths(1);
		int monthNumber = currentDate.getMonthValue();
		String monthName = getMonthName(monthNumber);

		return monthName;
	}

	/**
	 * Gibt den Namen des naechsten kommenden Monats zurueck.
	 * 
	 * @param year
	 * @param month
	 * @return String Name des kommenden Monats
	 * 
	 * @author Feras Ejneid
	 */
	public static String getNameOfNextMonth(int year, int month) {
		LocalDate currentDate = LocalDate.of(year, month, 1);
		currentDate = currentDate.plusMonths(1);
		int monthNumber = currentDate.getMonthValue();
		String monthName = getMonthName(monthNumber);

		return monthName;
	}

	/**
	 * Gibt den Namen vom Monat zurueck, der mit der uebergebenen Monatsnummer
	 * uebereinstimmt.
	 * 
	 * @param monthNumber
	 * @return String Monatname
	 * 
	 * @author Feras Ejneid
	 */
	public static String getMonthName(int monthNumber) {
		String monthName = "";
		switch (monthNumber) {
		case 1:
			monthName = JAN;
			break;
		case 2:
			monthName = FEB;
			break;
		case 3:
			monthName = MÄR;
			break;
		case 4:
			monthName = APR;
			break;
		case 5:
			monthName = MAI;
			break;
		case 6:
			monthName = JUN;
			break;
		case 7:
			monthName = JUL;
			break;
		case 8:
			monthName = AUG;
			break;
		case 9:
			monthName = SEP;
			break;
		case 10:
			monthName = OKT;
			break;
		case 11:
			monthName = NOV;
			break;
		case 12:
			monthName = DEZ;
			break;
		default:
			monthName = JAN;
			break;
		}
		return monthName;
	}

	/**
	 * das aktuelle Jahr zuruckgeben.
	 * 
	 * @return int Jahr
	 */
	public static int getCurrentYear() {
		return getCalearndarInstance().get(Calendar.YEAR);
	}

	/**
	 * die Name des akuellen Monats.
	 * 
	 * @return String Monatsname
	 */
	public static String getCurrentMonth() {
		int currentMonthNumber = getCalearndarInstance().get(Calendar.MONTH);
		String month = getMonthName(currentMonthNumber + 1);
		return month;
	}
}
