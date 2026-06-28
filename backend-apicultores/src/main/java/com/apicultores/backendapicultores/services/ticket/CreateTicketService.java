package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.enums.PaymentStatus;
import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.common.enums.TicketStatus;
import com.apicultores.backendapicultores.common.util.UtilsFunctions;
import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import com.apicultores.backendapicultores.exception.custom.*;
import com.apicultores.backendapicultores.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;



@Service
@AllArgsConstructor
public class CreateTicketService {
    private final UtilsFunctions functions;
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Transactional
    public List<TicketResponse> generateTicketsForReservation(Reservation reservation,
                                                              Payment payment){

        List<Ticket> existingTickets = ticketRepository.findByReservationWithEagerLoad(
                reservation.getReservationId()
        );

        if (!existingTickets.isEmpty()) {
            return existingTickets.stream()
                    .map(ticketMapper::toDto)
                    .toList();
        }

        long purchasedCount = ticketRepository.countTicketByUserAndEvent(
                reservation.getUser().getUserId(),
                reservation.getEvent().getEventId(),
                List.of(TicketStatus.USED, TicketStatus.PAID)
        );

        long totalSeats = purchasedCount + reservation.getSeats().size();

        if (totalSeats > reservation.getEvent().getMaxTicketsPerUser()){
            throw new LimitSeatsException("Ya se compraron la máxima cantidad de asientos por persona para este evento");
        }

        if (payment.getStatus() != PaymentStatus.COMPLETED){
            throw new PaymentStatusException("El pago asociado a la reserva no ha sido completado.");
        }
        if (reservation.getStatus() != ReservationStatus.COMPLETED){
            throw new ReservationStatusException("La reserva no ha sido pagada");
        }

        List<Seat> associatedSeats = reservation.getSeats();

        if(associatedSeats.isEmpty()){
            throw new EmptySeatsReservationException("La reserva no contiene asientos");
        }
        if (associatedSeats.size() > reservation.getEvent().getMaxTicketsPerUser()){
            throw new LimitSeatsException("Limite de reserva excedido");
        }

        List<TicketResponse> ticketResponsesList = new ArrayList<>();
        for (Seat seat : associatedSeats){
            String QrTicket = functions.makeQRInfo(seat.getSeatNumber());

            Ticket newTicket = ticketMapper.toEntityCreate(reservation,payment,seat,QrTicket);

            Ticket savedTicket = ticketRepository.save(newTicket);

            TicketResponse ticketResponse = ticketMapper.toDto(savedTicket);

            ticketResponsesList.add(ticketResponse);
        }

        return ticketResponsesList;

    }


}
