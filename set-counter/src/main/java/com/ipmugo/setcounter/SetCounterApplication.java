package com.ipmugo.setcounter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEurekaClient
@EnableMongoAuditing
@EnableScheduling
public class SetCounterApplication {
    public static void main(String[] args) {
        SpringApplication.run(SetCounterApplication.class, args);
    }
}