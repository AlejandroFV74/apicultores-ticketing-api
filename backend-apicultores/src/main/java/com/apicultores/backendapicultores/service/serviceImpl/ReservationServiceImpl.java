package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.common.mappers.ReservationMapper;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.CreateReservationRequest;
import com.apicultores.backendapicultores.domain.dto.response.ReservationResponse;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.domain.entity.User;
import com.apicultores.backendapicultores.exception.custom.UserNotFoundException;
import com.apicultores.backendapicultores.repository.ReservationRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import com.apicultores.backendapicultores.repository.UserRepository;
import com.apicultores.backendapicultores.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final SeatRepository seatRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request){
        List<Seat> seats = seatRepository.findAllById(
                request.getSeatsIds()
        );
        UUID userId = currentUserProvider.getCurrentUserId();

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Reservation reservation =
                reservationMapper.toEntityCreate(
                        user,
                        seats
                );

        return   reservationMapper.toDto(reservation);

    }
}
