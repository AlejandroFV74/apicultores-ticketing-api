package com.apicultores.backendapicultores.validation;

import com.apicultores.backendapicultores.domain.entity.Ticket;

public interface TicketValidationStep {
    void validate(Ticket ticket);
}
