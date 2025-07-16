package com.backend.distributedqueue.prioflow.service;

import com.backend.distributedqueue.exception.JobActivityException;
import com.google.protobuf.Timestamp;
import com.shared.protos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PrioFlowService {

    private static final Logger log = LoggerFactory.getLogger(PrioFlowService.class);

    /**
     * TODO : Introduce this feature later.
     * Atomically claims the best available priority rank from a shared pool in Redis.
     * <p>
     * This method resolves the critical challenge of assigning a unique resource in a distributed environment.
     * It uses the Redis `ZPOPMIN` command, an atomic write operation that guarantees a single rank
     * can only be claimed once across all service instances.
     * <p>
     * SCALING STRATEGY:
     * This implementation uses a single, well known key in Redis (e.g., "available_ranks"). This is simple
     * and highly performant for most use cases.
     * <p>
     * If this single key ever becomes a "hot key" bottleneck under extreme load (proven by monitoring),
     * the system can be scaled further by implementing client-side sharding: creating multiple rank pools
     * (e.g., "available_ranks_1", "available_ranks_2") and having the application randomly pick a pool
     * to claim a rank from. This would distribute the write load across the entire Redis cluster.
     * <p>
     * Alternatively we can do pessimistic-lock on rows from the database and assign the keys accordingly.
     */
    public void assignPriorityRank() {

    }


    /**
     * Processes a 'TASK_CREATE' action for a Priority Flow job.
     * This method now correctly extracts data from the incoming job, delegates
     * business logic, and returns a task object indicating the outcome.
     *
     * @param task      The Task containing a PriorityFlowPayload.
     * @param jobId     The parent Job ID for logging and context.
     * @param createdBy The user who created the job, for auditing.
     * @return An updated Task reflecting the outcome (SUCCESS or FAILURE).
     */
    public Task createTask(Task task, String jobId, String createdBy) {
        log.info("Executing CREATE task for PrioFlow with Job ID: {}", jobId);
        PriorityFlowPayload payload = task.getPriorityFlowPayload();

        try {
            // assignPriorityRank(task);
            // --- Business Logic for this task goes here ---
            // For example: claim a rank from Redis, call an external API, etc.
            // We will simulate this by populating the payload with new data.
            PriorityFlowPayload populatedPayload = payload.toBuilder()
                    .setPrioFId(UUID.randomUUID().toString())
                    .setPrioFName("Processed: " + payload.getPrioFName())
                    .setPrioFRank(100) // Example rank from a claim
                    .build();

            // Return a new Task object reflecting the successful outcome.
            return task.toBuilder()
                    .setTaskId(UUID.randomUUID().toString())
                    .setTaskAction(TaskAction.TASK_SUCCESS)
                    .setTaskLastModifiedBy(createdBy)
                    .setTaskLastModifiedTimeStamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                    .setPriorityFlowPayload(populatedPayload)
                    .setTaskDescription("Successfully processed new priority flow: " + payload.getPrioFName())
                    .build();

        } catch (Exception e) {
            log.error("Business logic failed for PrioFlow Task on Job ID: {}. Error: {}", jobId, e.getMessage(), e);

            // In case of failure, return a Task object with a failure status.
            return task.toBuilder()
                    .setTaskAction(TaskAction.TASK_FAILURE)
                    .setTaskLastModifiedBy(createdBy)
                    .setTaskLastModifiedTimeStamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                    .setTaskDescription("Failed to process PrioFlow task: " + e.getMessage())
                    .build();
        }
    }
}
