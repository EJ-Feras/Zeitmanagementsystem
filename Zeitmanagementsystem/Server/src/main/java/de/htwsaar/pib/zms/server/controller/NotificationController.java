package de.htwsaar.pib.zms.server.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.htwsaar.pib.zms.server.model.Notification;
import de.htwsaar.pib.zms.server.model.User;
import de.htwsaar.pib.zms.server.model.UserPasswordStruct;
import de.htwsaar.pib.zms.server.service.NotificationService;

@RestController
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	@GetMapping("/notification/{id}")
	public Optional<Notification> getnotification(@PathVariable long id) {
		return notificationService.getNotificationById(id);
	}

	@GetMapping("/notification/{userId}/{count}")
	public List<Notification> getLatestNotifications(@PathVariable long userId, @PathVariable int count) {
		return notificationService.getLatestNotification(userId, count);
	}

	@PostMapping("/notification")
	public void addNotification(@RequestBody Notification notification) {
		notificationService.addNotification(notification);
	}
	
	@PutMapping("/notification")
	public void setNotificationSeen(@RequestBody Notification notification) {
		notificationService.setNotificationSeen(notification);
	}

	@DeleteMapping("/notification/{id}")
	public void deleteNotification(@PathVariable long notificationId) {
		notificationService.deletNotificationById(notificationId);
	}
}
