package notification;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.Repetitiontype;
import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.model.UserRole;
import de.htwsaar.pib.zms.server.service.EventService;
import de.htwsaar.pib.zms.server.service.NotificationService;
import de.htwsaar.pib.zms.server.service.UserService;

public class NotificationServerTest {

	@Autowired
	private static NotificationService notiser = new NotificationService();
	
	@Autowired
	private static UserService userser = new UserService();
	
	@Autowired
	private static EventService eventser = new EventService();
	
	
	public static void main(String[] args) {
		User newUser = new User("Udo", "Mustermann", "testUser", UserRole.USER ,"therealudo@web.de");
		
		userser.addUser(newUser, "hi");
		
		Event event = new Event("TestEvent", "Event aus der Testmethode", new Date(), 10, Repetitiontype.NEVER, newUser);
		
		eventser.save(event);
		
		notiser.addNotification(Notification.createInvitation(newUser, event));
	}
}
