package com.apicultores.backendapicultores.validation.validationSteps;

import com.apicultores.backendapicultores.exceptions.TicketStatusException;
import com.apicultores.backendapicultores.domain.entities.Ticket;
import com.apicultores.backendapicultores.common.TicketStatus;
import com.apicultores.backendapicultores.validation.TicketValidationStep;
import org.springframework.stereotype.Component;

@Component
public class StatusValidationStep implements TicketValidationStep {
    //CAMBIAR POR ESTADO DE PAYMENT CUANDO ESTE IMPLEMENTADO
    @Override
    public void validate(Ticket ticket) {
        if (ticket.getStatus() != TicketStatus.PAID){
            throw new TicketStatusException("El ticket no tiene un estado válid: " + ticket.getStatus());
        }
    }
}
