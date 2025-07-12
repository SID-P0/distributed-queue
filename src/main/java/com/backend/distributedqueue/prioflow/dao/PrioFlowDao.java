package com.backend.distributedqueue.prioflow.dao;

import com.backend.distributedqueue.prioflow.dto.JobEntity;
import com.backend.distributedqueue.prioflow.dto.PriorityFlowPayloadEntity;
import com.backend.distributedqueue.prioflow.dto.TaskEntity;
import com.backend.distributedqueue.prioflow.repository.JobRepository;
import com.google.protobuf.Timestamp;
import com.shared.protos.Job;
import com.shared.protos.PriorityFlowPayload;
import com.shared.protos.TaskMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PrioFlowDao {

    private final JobRepository jobRepository;

    /**
     * Persists a complete Job, including its Task and specific PriorityFlowPayload,
     * within a single transaction, mapping directly from the protobuf object.
     *
     * @param job The protobuf Job message to persist.
     * @return The persisted JobEntity, including its cascaded children.
     */
    @Transactional
    public JobEntity savePriorityFlowJob(Job job) {
        // 1. Extract the nested payload and metadata from the protobuf message
        PriorityFlowPayload payload = job.getPriorityFlowPayload();
        TaskMetadata taskMetadata = payload.getTaskMetadata();

        // 2. Create the top-level JobEntity
        JobEntity jobEntity = new JobEntity();
        jobEntity.setJobId(UUID.fromString(job.getJobId()));
        jobEntity.setJobName(job.getJobName());
        jobEntity.setJobAction(job.getJobAction().name());
        jobEntity.setJobDescription(job.getJobDescription());
        jobEntity.setCreatedBy(job.getCreatedBy());
        jobEntity.setCreationTimestamp(convertTimestamp(job.getCreationTimeStamp()));

        // 3. Create the generic TaskEntity
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskId(UUID.fromString(taskMetadata.getTaskId()));
        taskEntity.setTaskType(job.getJobType().name()); // Use JobType as the discriminator
        taskEntity.setTaskAction(taskMetadata.getTaskAction().name());
        taskEntity.setTaskDescription(taskMetadata.getTaskDescription());
        taskEntity.setTaskLastModifiedBy(taskMetadata.getTaskLastModifiedBy());
        taskEntity.setTaskLastModifiedTimestamp(convertTimestamp(taskMetadata.getTaskLastModifiedTimeStamp()));

        // 4. Create the specific PriorityFlowPayloadEntity
        PriorityFlowPayloadEntity payloadEntity = new PriorityFlowPayloadEntity();
        payloadEntity.setPrioFId(payload.getPrioFId());
        payloadEntity.setPrioFRank(payload.getPrioFRank());
        payloadEntity.setPrioFName(payload.getPrioFName());

        // 5. Establish the relationships between the entities for cascading persistence
        jobEntity.setTasks(Collections.singletonList(taskEntity));
        taskEntity.setJob(jobEntity);
        taskEntity.setPriorityFlowPayload(payloadEntity);
        payloadEntity.setTask(taskEntity);

        // 6. Save the aggregate root (JobEntity). JPA's cascading will persist the children.
        return jobRepository.save(jobEntity);
    }

    /**
     * Helper to convert Google's Protobuf Timestamp to Java's OffsetDateTime for PostgreSQL.
     */
    private OffsetDateTime convertTimestamp(Timestamp protoTimestamp) {
        if (protoTimestamp == null || (protoTimestamp.getSeconds() == 0 && protoTimestamp.getNanos() == 0)) {
            return OffsetDateTime.now(ZoneOffset.UTC);
        }
        Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());
        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
