package com.backend.distributedqueue.controller;

import com.backend.distributedqueue.models.JobActionResponse;
import com.backend.distributedqueue.validation.JobRequestValidation;
import com.google.protobuf.InvalidProtocolBufferException;
import com.shared.protos.Job;
import com.shared.protos.JobAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor; // Import for constructor injection
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Tag(name = "Job Management", description = "API's for managing and monitoring jobs")
@RequiredArgsConstructor // 1. Use constructor injection
public class JobRestController {
    private static final Logger log = LoggerFactory.getLogger(JobRestController.class);

    // 2. The dependency is now final and injected via the constructor
    private final JobRequestValidation jobRequestValidation;

    @Operation(summary = "Health Check", description = "Checks the health of the application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is healthy",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }


    @Operation(
            summary = "Upload and submit a Protobuf job (via UI or Swagger)",
            description = "Accepts a binary Protobuf job file along with a jobAction, both via multipart/form-data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Job submitted",
                    content = @Content(schema = @Schema(implementation = JobActionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad input: Invalid file format or unknown jobAction",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/handleJob", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobActionResponse> handleJob(
            @Parameter(description = "Binary Protobuf-encoded job file", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Action to perform on the job", required = true, schema = @Schema(implementation = JobAction.class))
            // 3. Let Spring convert the String to an Enum automatically.
            @RequestParam("jobAction") JobAction jobAction) {

        if (file.isEmpty()) {
            // It's good practice to throw a specific exception that a @ControllerAdvice can handle.
            throw new IllegalArgumentException("Protobuf file part cannot be empty.");
        }

        try {
            Job job = Job.parseFrom(file.getInputStream());

            log.info("Received job type={} with action={} from user={}", job.getJobType(), jobAction, job.getCreatedBy());
            jobRequestValidation.validateAndPublishJob(job, jobAction);

            String msg = String.format("Job of type '%s' submitted by '%s' was accepted.", job.getJobType(), job.getCreatedBy());
            return ResponseEntity.status(HttpStatus.CREATED).body(new JobActionResponse(msg, job.getJobId()));

        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse Protobuf file. It may be corrupted or not a valid Job message.", e);
            throw new IllegalArgumentException("The provided file is not a valid Protobuf Job message.");
        } catch (IOException e) {
            log.error("Could not read the uploaded file stream.", e);
            throw new RuntimeException("An I/O error occurred while reading the uploaded file.");
        }
    }
}