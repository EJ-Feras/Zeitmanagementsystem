package de.htwsaar.pib.zms.server.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import de.htwsaar.pib.zms.server.model.*;
import de.htwsaar.pib.zms.server.service.EventService;

@RestController
public class EventController {

	@Autowired
	private final EventService eventService;

	EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping("/event")
	List<Event> all() {
		return eventService.getAllEvents();
	}

	@GetMapping("user/{username}/event/createdEvents")
	List<Event> findCreatedEvents(@PathVariable String username) {
		return eventService.findCreatedEvents(username);
	}

	@PostMapping("/event")
	Event newEvent(@RequestBody Event newEvent) {
		return eventService.save(newEvent);
	}

	@GetMapping("/event/{id}")
	Event one(@PathVariable Long id) {

		return eventService.getEventById(id);
	}

	@GetMapping("/user/{username}/event/upcomingEvents")
	List<Event> getUpcomingEvents(@PathVariable String username){
		return eventService.getUpcomingEvents(username);
	}
		
	@PutMapping("/event")
	Event replaceEvent(@RequestBody Event newEvent) {
		return eventService.changeEvent(newEvent);
	}

	@DeleteMapping("/event/{id}")
	void deleteEvent(@PathVariable Long id) {
		eventService.deleteById(id);
	}
}