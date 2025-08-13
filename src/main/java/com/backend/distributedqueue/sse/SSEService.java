package com.backend.distributedqueue.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.google.protobuf.util.JsonFormat;
import com.shared.protos.Job;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SSEService {

    // Use a thread-safe list to hold all active emitters for the global stream.
    // CopyOnWriteArrayList is highly efficient for broadcast scenarios (many reads, few writes).
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private static final long EMITTER_TIMEOUT = 30 * 60 * 1000L; // 30 minutes

    /**
     * Creates and registers a new SseEmitter for the global job update stream.
     *
     * @return The configured SseEmitter for the controller to return.
     */
    public SseEmitter createGlobalEmitter() {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);

        // Add the new emitter to our list of subscribers.
        this.emitters.add(emitter);
        log.info("Created and registered new SSE emitter for the global stream. Total subscribers: {}", emitters.size());

        // On completion, timeout, or error, remove the emitter from the list to prevent memory leaks.
        Runnable removeEmitter = () -> {
            this.emitters.remove(emitter);
            log.info("Global stream emitter removed. Remaining subscribers: {}", emitters.size());
        };
        emitter.onCompletion(removeEmitter);
        emitter.onTimeout(removeEmitter);
        emitter.onError(e -> {
            log.error("Global stream emitter errored out: {}", e.getMessage());
            removeEmitter.run();
        });

        // Send an initial "connection established" event.
        try {
            emitter.send(SseEmitter.event().name("GLOBAL_STREAM_INIT").data("Connection to global job stream established."));
        } catch (IOException e) {
            log.error("Failed to send initial SSE event to new global subscriber.", e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * Broadcasts a job status update to ALL connected SSE clients.
     * This method should be called by the JobOrchestrator.
     *
     * @param job The Job object with the updated status.
     */
    public void broadcastEvent(Job job) {
        if (emitters.isEmpty()) {
            log.debug("No active SSE subscribers to broadcast event for jobId: {}", job.getJobId());
            return;
        }

        log.info("Broadcasting update for jobId: {} to {} subscribers.", job.getJobId(), emitters.size());
        String jsonJob;
        try {
            // Convert the Protobuf Job object to a JSON string once for efficiency.
            jsonJob = JsonFormat.printer().print(job);
        } catch (Exception e) {
            log.error("Failed to serialize Job protobuf to JSON for broadcast. JobId: {}", job.getJobId(), e);
            return; // Cannot proceed if serialization fails.
        }

        // Iterate over a snapshot of the list to send the event.
        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event().name("JOB_UPDATE").data(jsonJob));
            } catch (Exception e) {
                // This client is likely disconnected. The onCompletion/onError handlers will eventually remove it.
                log.warn("Failed to send event to a specific emitter. It will be removed. Error: {}", e.getMessage());
            }
        }
    }
}
