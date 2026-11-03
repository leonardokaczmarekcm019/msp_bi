package com.example.msp_bi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MspBiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MspBiApplication.class, args);
    }

}