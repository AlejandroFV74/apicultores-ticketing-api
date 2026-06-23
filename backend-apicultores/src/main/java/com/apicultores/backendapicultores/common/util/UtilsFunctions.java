package com.apicultores.backendapicultores.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UtilsFunctions {
    public String makeQRInfo(String seat){
        UUID randomCode = UUID.randomUUID();
        return randomCode+seat;
    }
}
