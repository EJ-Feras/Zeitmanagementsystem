package de.htwsaar.pib.zms.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import de.htwsaar.pib.zms.server.auth.PasswordGenerator;
import de.htwsaar.pib.zms.server.model.Event;
import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.model.UserRole;
import de.htwsaar.pib.zms.server.repository.EventRepository;
import de.htwsaar.pib.zms.server.repository.NotificationRepository;
import de.htwsaar.pib.zms.server.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private EventRepository eventRepo;

	public List<User> getAllUser() {
		return userRepo.findAll();
	}

	public List<User> findAll() {
		List<User> result = new ArrayList<User>();
		for (User u : userRepo.findAll()) {
			result.add(u);
		}
		return result;
	}

	public User getUserByUsername(String username) {
		User u = userRepo.findByUsername(username);
		return u;
	}

	public User getUserByid(long id) {
		Optional<User> user = userRepo.findById(id);
		return user.get();

	}

	public void addUser(User user, String password) {
		List<User> users = userRepo.findAll();
		users.stream().filter(s -> s.getPassword() != password);
		for (User e : users)
			if (e.equals(user)) {
				throw new IllegalArgumentException("Dieses User ist Existiert");
			}
		user.setPassword(PasswordGenerator.generatePW(password));
		userRepo.save(user);
	}

	public void deleteUserById(long id) {
		Optional<User> user = userRepo.findById(id);
		if (user == null) {
			throw new IllegalArgumentException("Dieses User Existiert nicht");
		}
		userRepo.deleteById(id);
	}

	public void delete(User user) {
		userRepo.delete(user);
	}

	public User changeUser(User u) {

		if (!isValidExistingUser(u)) {
			return null;
		}

		Optional<User> old = userRepo.findById(u.getId());
		return changeUserAndPassword(u, old.get().getPassword());
	}

	public User changeUserAndPassword(User user, String password) {
		user.setPassword(password);
		user = userRepo.save(user);
		ServerSentEventsService.notifyAboutChangeIn(user);
		return user;
	}

	public void deletebyUsername(String username) {
		User u = userRepo.findByUsername(username);
		if (u.getRole() != UserRole.SUPERUSER) {
			List<Event> upcomingEvents = u.getUpcomingEvents();
			for (Event event : upcomingEvents) {
				event.getParticipants().remove(u);
				eventRepo.save(event);
			}
			userRepo.deleteById(u.getId());
		}
	}

	public boolean isValidExistingUser(User user) {
		if (user.getId() == 0) {
			return false;
		}
		if (!userRepo.existsById(user.getId())) {
			return false;
		}
		return true;
	}

}
