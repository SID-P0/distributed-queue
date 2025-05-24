package com.backend.distributedqueue;

import com.shared.protosnomo.Job;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistributedQueueApplication {
    public static void main(String[] args) {
        SpringApplication.run(DistributedQueueApplication.class, args);
    }
}