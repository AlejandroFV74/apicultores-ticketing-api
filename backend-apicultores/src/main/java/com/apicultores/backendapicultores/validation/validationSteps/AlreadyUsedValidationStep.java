package com.apicultores.backendapicultores.validation.validationSteps;

import com.apicultores.backendapicultores.exceptions.TicketStatusException;
import com.apicultores.backendapicultores.domain.entities.Ticket;
import com.apicultores.backendapicultores.domain.entities.enums.TicketStatus;
import com.apicultores.backendapicultores.validation.TicketValidationStep;

public class AlreadyUsedValidationStep implements TicketValidationStep {
    @Override
    public void validate(Ticket ticket) {
        if(ticket.getStatus().equals(TicketStatus.USED) || ticket.getUsedAt() != null){
            throw new TicketStatusException("El ticket ya ha sido usado");
        }
    }
}
