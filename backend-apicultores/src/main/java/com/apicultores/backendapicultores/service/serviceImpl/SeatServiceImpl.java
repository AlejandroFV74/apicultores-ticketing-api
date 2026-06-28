package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.domain.dto.request.CreateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.response.SeatResponse;
import com.apicultores.backendapicultores.domain.entity.Event;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.common.mappers.SeatMapper;
import com.apicultores.backendapicultores.repository.EventRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import com.apicultores.backendapicultores.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final SeatMapper seatMapper;

    @Override
    @Transactional
    public SeatResponse createSeat(CreateSeatRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        Seat seat = seatMapper.toEntity(request, event);
        Seat savedSeat = seatRepository.save(seat);

        return seatMapper.toDto(savedSeat);
    }

    @Override
    public List<SeatResponse> getAllSeats() {
        List<Seat> seats = seatRepository.findAll();
        if (seats.isEmpty()) {
            throw new ResourceNotFoundException("No seats found");
        }
        return seats.stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @Override
    public SeatResponse getSeatById(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
        return seatMapper.toDto(seat);
    }

    @Override
    public List<SeatResponse> getSeatsByEventId(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        List<Seat> seats = seatRepository.findAll().stream()
                .filter(seat -> seat.getEvent().getEventId().equals(eventId))
                .toList();

        if (seats.isEmpty()) {
            throw new ResourceNotFoundException("No seats found for this event");
        }

        return seats.stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SeatResponse updateSeat(UUID seatId, UpdateSeatRequest request) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        Seat updatedSeat = seatMapper.toEntityUpdate(request, seat);
        Seat savedSeat = seatRepository.save(updatedSeat);

        return seatMapper.toDto(savedSeat);
    }

    @Override
    @Transactional
    public void deleteSeat(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
        seatRepository.delete(seat);
    }
}
