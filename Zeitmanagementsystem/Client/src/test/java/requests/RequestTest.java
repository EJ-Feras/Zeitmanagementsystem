package requests;

import de.htwsaar.pib.vs.zms.client.service.RequestService;
import de.htwsaar.pib.vs.zms.client.exceptions.*;
import de.htwsaar.pib.zms.server.model.User;

public class RequestTest {
	
	public static void main (String[] args) {
		RequestService test = RequestService.getInstance();
		//RequestService test = RequestService.getInstance("https://c2d41ed0-22fa-4295-8e72-1a5c8f29a917.mock.pstmn.io");
		User udo = null;
		try {
			udo = test.getUser("root", "rootpw");
		} catch (UnknownObjectException | WrongUserDetailsException | NoConnectionToServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(udo.getId());
	}
}


