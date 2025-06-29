package com.backend.distributedqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication()
@PropertySource("classpath:secrets.properties")
public class DistributedQueueApplication {
    public static void main(String[] args) {
        SpringApplication.run(DistributedQueueApplication.class, args);
    }
}