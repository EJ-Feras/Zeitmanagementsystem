package de.htwsaar.pib.zms.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import de.htwsaar.pib.zms.server.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	public Optional<Notification> findById(long id);

}
