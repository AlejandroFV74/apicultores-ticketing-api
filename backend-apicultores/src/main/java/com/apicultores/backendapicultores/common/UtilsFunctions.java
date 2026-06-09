package com.apicultores.backendapicultores.common;

import java.time.LocalDateTime;

public class UtilsFunctions {
    public String makeQRInfo(String username, Integer seat){
        return username+seat.toString()+LocalDateTime.now();
    }
}
