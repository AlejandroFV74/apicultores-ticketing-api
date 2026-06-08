package com.apicultores.backendapicultores.validation;

import com.apicultores.backendapicultores.domain.entities.Ticket;

public interface TicketValidationStep {
    void validate(Ticket ticket);
}
