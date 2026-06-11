package com.apicultores.backendapicultores.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UtilsFunctions {
    public String makeQRInfo(String username, String seat){
        return username+seat+LocalDateTime.now();
    }
}
