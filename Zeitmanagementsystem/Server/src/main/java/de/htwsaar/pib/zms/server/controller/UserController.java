package de.htwsaar.pib.zms.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.model.UserPasswordStruct;
import de.htwsaar.pib.zms.server.service.ServerSentEventsService;
import de.htwsaar.pib.zms.server.service.UserService;

@RestController
public class UserController {

	private final UserService userservice;

	@Autowired
	public UserController(UserService userservice) {
		this.userservice = userservice;
	}

	@GetMapping("users")
	public List<User> getAllUser() {
		return userservice.getAllUser();
	}

	@GetMapping("user/{username}")
	public User getUser(@PathVariable String username) {
		return userservice.getUserByUsername(username);
	}

	@PutMapping(value = "user")
	public User changeUser(@RequestBody UserPasswordStruct u) {
		if (u.password.isEmpty()) {
			return userservice.changeUser(u.user);
		}
		else {
			return userservice.changeUserAndPassword(u.user, u.password);
		}
	}

	@PostMapping(value = "user")
	public void addUser(@RequestBody UserPasswordStruct up) {
		userservice.addUser(up.user, up.password);
	}

	@DeleteMapping(value = "user/{username}")
	public void deleteUser(@PathVariable String username) {
		userservice.deletebyUsername(username);
	}
	
	
	
	@GetMapping("SseEmitter/{username}")
	public SseEmitter subscribe (@PathVariable String username) {
		return ServerSentEventsService.subscribe(username);
	}

	@PutMapping("SseEmitter/{username}")
	public void unsubscribe (@PathVariable String username) {
		ServerSentEventsService.unsubscribe(username);
	}
}
