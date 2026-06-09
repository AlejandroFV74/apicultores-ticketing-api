package com.apicultores.backendapicultores.services.ticket;

import com.apicultores.backendapicultores.common.ReservationStatus;
import com.apicultores.backendapicultores.common.UtilsFunctions;
import com.apicultores.backendapicultores.common.mappers.TicketMapper;
import com.apicultores.backendapicultores.domain.dto.request.CreateTicketRequest;
import com.apicultores.backendapicultores.domain.dto.response.ticket.TicketResponse;
import com.apicultores.backendapicultores.domain.entities.Reservation;
import com.apicultores.backendapicultores.domain.entities.Seat;
import com.apicultores.backendapicultores.domain.entities.Ticket;
import com.apicultores.backendapicultores.exceptions.EmptySeatsReservationException;
import com.apicultores.backendapicultores.exceptions.ReservationNotFoundException;
import com.apicultores.backendapicultores.exceptions.ReservationStatusException;
import com.apicultores.backendapicultores.repositories.ReservationRepository;
import com.apicultores.backendapicultores.repositories.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
@AllArgsConstructor
public class CreateTicketService {
    UtilsFunctions functions;
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final ReservationRepository reservationRepository;

    public List<TicketResponse> generateTicketsForReservation(CreateTicketRequest ticketRequest){
        Reservation reservation = reservationRepository.findById(ticketRequest.getReservationId())
                .orElseThrow(()-> new ReservationNotFoundException("La reserva con dicho Id no se encuentra"));

        if (reservation.getStatus() != ReservationStatus.PAID){
            throw new ReservationStatusException("No podemos emitir el ticket si la reserva no está pagada");
        }

        List<Seat> associatedSeats = reservation.getSeats();
        if(associatedSeats.isEmpty()){
            throw new EmptySeatsReservationException("La reserva no contiene asientos");
        }

        List<TicketResponse> ticketResponsesList = new ArrayList<>();

        for (Seat seat : associatedSeats){
            String QrTicket = functions.makeQRInfo(reservation.getUser().getName(),seat.getSeatNumber());

            Ticket newTicket = ticketMapper.toEntityCreate(reservation,seat,QrTicket);

            Ticket savedTicket = ticketRepository.save(newTicket);

            TicketResponse ticketResponse = ticketMapper.toDto(savedTicket);

            ticketResponsesList.add(ticketResponse);
        }

        return ticketResponsesList;

    }


}
