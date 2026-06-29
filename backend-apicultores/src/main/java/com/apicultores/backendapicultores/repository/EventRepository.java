package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.common.enums.EventStatus;
import com.apicultores.backendapicultores.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByOrderByStartDateAsc();

    List<Event> findByOrganizerId(UUID organizerId);

    List<Event> findByOrganizerIdOrderByStartDateAsc(UUID organizerId);

    List<Event> findByTitleContainingIgnoreCase(String title);

    List<Event> findByStatusAndStartDateAfterOrderByStartDateAsc(EventStatus status, LocalDateTime startDate);

    List<Event> findByStatusAndStartDateAfterAndTitleContainingIgnoreCaseOrderByStartDateAsc(
            EventStatus status,
            LocalDateTime startDate,
            String title
    );
}
