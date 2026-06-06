package com.apicultores.backendapicultores.validation;

import com.apicultores.backendapicultores.model.Ticket;

public interface TicketValidationStep {
    void validate(Ticket ticket);
}
