package de.htwsaar.pib.zms.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.htwsaar.pib.zms.server.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	public List<Event> findByTitle(String title);

	@Query("SELECT e FROM Event e WHERE e.eventCreator.id =:userId")
	public List<Event> findCreatedEvents(long userId);
}
