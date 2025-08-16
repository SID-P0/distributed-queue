package com.backend.distributedqueue.controller;

import com.backend.distributedqueue.sse.SSEService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job Status", description = "API for streaming real-time job status updates")
@RequiredArgsConstructor
public class SSEController {
    private final SSEService sseService;

    @Operation(
            summary = "Subscribe to a global stream of all job status updates via SSE",
            description = "Opens a Server-Sent Events (SSE) stream that broadcasts updates for ALL jobs in the system."
    )
    @ApiResponse(responseCode = "200", description = "Global event stream opened successfully.")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAllJobUpdates() {
        return sseService.createGlobalEmitter();
    }
}
