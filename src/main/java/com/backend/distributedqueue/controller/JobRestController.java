package com.backend.distributedqueue.controller;

import com.backend.distributedqueue.handler.JobRequestHandler;
import com.shared.protos.Job;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Job Management", description = "API's for managing and monitoring jobs")
public class JobRestController {
    private static final Logger log = LoggerFactory.getLogger(JobRestController.class);

    @Autowired
    private JobRequestHandler jobRequestHandler;

    @Operation(summary = "Health Check", description = "Checks the health of the application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is healthy",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Submit a new Job", description = "Submits a new job to the distributed queue for processing (expects binary Protobuf).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job created successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))),
            // Consider defining a proper ErrorResponse DTO for 400 and 500 errors
            @ApiResponse(responseCode = "400", description = "Invalid job payload provided or malformed Protobuf",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/createJob", consumes = "application/x-protobuf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitJob(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Job object to be created (serialized binary Protobuf)",
                    required = true,
                    content = @Content(
                            mediaType = "application/x-protobuf",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestBody Job job) {
        log.info("Received request to create job with ID: {}", job.getJobId());
        jobRequestHandler.handleJob(job);
        log.info("Job with ID: {} processed and created successfully.", job.getJobId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Job created successfully with ID: " + job.getJobId());
    }
}