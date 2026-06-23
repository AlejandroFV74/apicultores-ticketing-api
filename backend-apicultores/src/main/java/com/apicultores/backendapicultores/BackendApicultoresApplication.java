package com.apicultores.backendapicultores;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendApicultoresApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApicultoresApplication.class, args);
    }

}
