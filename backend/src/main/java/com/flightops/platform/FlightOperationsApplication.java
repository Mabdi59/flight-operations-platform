package com.flightops.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FlightOperationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightOperationsApplication.class, args);
    }
}
