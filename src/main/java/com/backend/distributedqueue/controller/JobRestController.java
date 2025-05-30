package com.backend.distributedqueue.controller;

import com.backend.distributedqueue.handler.JobRequestHandler;
import com.shared.protos.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobRestController {
    private static final Logger log = LoggerFactory.getLogger(JobRestController.class);

    @Autowired
    private JobRequestHandler jobRequestHandler;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/createJob")
    public ResponseEntity<String> submitJob(@RequestBody Job job) {
        jobRequestHandler.handleJob(job);
        log.info("Received job: " + job.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body("Job created successfully with ID: " + job.getJobId());
    }


}
