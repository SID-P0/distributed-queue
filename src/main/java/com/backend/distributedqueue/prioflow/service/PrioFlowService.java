package com.backend.distributedqueue.prioflow.service;

import com.backend.distributedqueue.factory.TaskProcessor;
import com.backend.distributedqueue.prioflow.dao.PrioFlowDao;
import com.google.protobuf.Timestamp;
import com.shared.protos.Job;
import com.shared.protos.PriorityFlowPayload;
import com.shared.protos.TaskAction;
import com.shared.protos.TaskMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PrioFlowService implements TaskProcessor {


    private static final Logger log = LoggerFactory.getLogger(PrioFlowService.class);
    private final PrioFlowDao prioFlowDao; // Inject the DAO

    public PrioFlowService(PrioFlowDao prioFlowDao) {
        this.prioFlowDao = prioFlowDao;
    }
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
     * persistence to the DAO, and returns a job object indicating the outcome.
     *
     * @param job The fully-formed Job object with a PriorityFlowPayload.
     * @return A new Job object with an updated task status (e.g., TASK_SUCCESS or TASK_FAILURE).
     */
    @Override
    public Job createTask(Job job) {
        log.info("Executing CREATE task for PrioFlow Job ID: {}", job.getJobId());
        // Extract payload for logging and response building
        PriorityFlowPayload payload = job.getPriorityFlowPayload();

        Instant now = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();

        try {
            // processJob(); This would refer to having the task do business logic on it.

            // 1. Delegate the entire persistence logic to the DAO.
            // The DAO will extract all necessary fields and save them.
            prioFlowDao.savePriorityFlowJob(job);
            log.info("Successfully persisted PrioFlow with Job ID: {}", job.getJobId());

            // 2. Build the success response.
            // Create updated metadata to reflect the successful outcome.

            TaskMetadata taskMetadata = payload.getTaskMetadata().toBuilder()
                    .setTaskAction(TaskAction.TASK_CREATE)
                    .setTaskLastModifiedBy(job.getCreatedBy())
                    .setTaskLastModifiedTimeStamp(timestamp)
                    .setTaskDescription("Successfully persisted new priority flow: " + payload.getPrioFName())
                    .build();

            PriorityFlowPayload successPayload = payload.toBuilder()
                    .setTaskMetadata(taskMetadata)
                    .build();

            // Return a new Job object with the updated payload.
            return job.toBuilder()
                    .setPriorityFlowPayload(successPayload)
                    .build();

        } catch (Exception e) {
            log.error("Failed to persist PrioFlow Job ID: {}. Error: {}", job.getJobId(), e.getMessage(), e);

            // 3. Build the failure response.
            TaskMetadata failureMetadata = payload.getTaskMetadata().toBuilder()
                    .setTaskAction(TaskAction.TASK_FAILURE)
                    .setTaskLastModifiedBy(job.getCreatedBy())
                    .setTaskLastModifiedTimeStamp(timestamp)
                    .setTaskDescription("Failed to save job to the database: " + e.getMessage())
                    .build();

            PriorityFlowPayload failurePayload = payload.toBuilder()
                    .setTaskMetadata(failureMetadata)
                    .build();

            // In a real system, you might re-throw a custom exception or just return the failure state.
            return job.toBuilder()
                    .setPriorityFlowPayload(failurePayload)
                    .build();
        }
    }

    /**
     * This is the crucial link. It declares that this service is responsible
     * for any task within a Job that has a 'priority_flow_payload'.
     */
    @Override
    public Job.PayloadCase getSupportedPayloadCase() {
        return Job.PayloadCase.PRIORITY_FLOW_PAYLOAD;
    }
}
