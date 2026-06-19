package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByOrganizerId(UUID organizerId);

    List<Event> findByTitleContainingIgnoreCase(String title);
}
