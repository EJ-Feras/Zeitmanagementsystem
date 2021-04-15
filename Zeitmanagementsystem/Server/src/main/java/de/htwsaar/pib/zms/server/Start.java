package de.htwsaar.pib.zms.server;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.model.UserRole;

import de.htwsaar.pib.zms.server.service.EventService;
import de.htwsaar.pib.zms.server.service.UserService;

@SpringBootApplication()
public class Start {

	@Autowired
	UserService userService;
	@Autowired
	EventService eventService;

	public static void main(String[] args) {
		SpringApplication.run(Start.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initAfterStartup() {
		User user1FromDb = userService.getUserByUsername("root");
		if (user1FromDb == null) {
			User user = new User("root", "root", "root", UserRole.SUPERUSER, "root@gmail.com");
			String pw1 = "123";
			user.setEnabled(true);
			userService.addUser(user, pw1);
		}
		System.out.println("Initialization successed !");
	}

	private Date createDate(int year, int month, int day, int hour, int min) {
		Calendar calender = Calendar.getInstance();
		calender.set(Calendar.YEAR, year);
		calender.set(Calendar.MONTH, month);
		calender.set(Calendar.DAY_OF_MONTH, day);
		calender.set(Calendar.HOUR_OF_DAY, hour);
		calender.set(Calendar.MINUTE, min);
		calender.set(Calendar.SECOND, 0);
		calender.set(Calendar.MILLISECOND, 0);
		Date date = calender.getTime();

		return date;
	}

}