package com.lyf.seexp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeExpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeExpApplication.class, args);
    }

}
