package com.backend.distributedqueue.dao;

import com.backend.distributedqueue.datapopulationjob.models.DataPopulationPayloadEntity;
import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.models.JobEntity;
import com.backend.distributedqueue.prioflow.models.PriorityFlowPayloadEntity;
import com.backend.distributedqueue.models.TaskEntity;
import com.backend.distributedqueue.prioflow.repository.JobRepository;
import com.google.protobuf.Timestamp;
import com.shared.protos.Job;
import com.shared.protos.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JobFlowDao {

    private final JobRepository jobRepository;

    /**
     * Persists a complete Job and its associated Tasks from the Protobuf message
     * into the database within a single transaction. This method is polymorphic and
     * can handle different task payload types.
     *
     * @param job The protobuf Job message to persist.
     * @return The persisted JobEntity, including its cascaded children.
     */
    @Transactional
    public void saveJob(Job job) {
        // 1. Create the top-level JobEntity from the protobuf message
        if (job.getJobId().isEmpty()) {
            throw new JobActivityException("Job already exists");
        }
        JobEntity jobEntity = new JobEntity();
        jobEntity.setJobId(UUID.fromString(job.getJobId()));
        jobEntity.setJobName(job.getJobName());
        jobEntity.setJobAction(job.getJobAction().name());
        jobEntity.setJobDescription(job.getJobDescription());
        jobEntity.setCreatedBy(job.getCreatedBy());
        jobEntity.setCreationTimestamp(convertTimestamp(job.getCreationTimeStamp()));

        List<TaskEntity> taskEntities = new ArrayList<>();

        // 2. Iterate through each task in the protobuf and create corresponding entities
        for (com.shared.protos.Task protoTask : job.getTasksList()) {
            // 3. Create the generic TaskEntity
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setTaskId(UUID.fromString(protoTask.getTaskId()));
            taskEntity.setTaskType(protoTask.getPayloadCase().name()); // Use payload case as the type discriminator
            taskEntity.setTaskAction(protoTask.getTaskAction().name());
            taskEntity.setTaskDescription(protoTask.getTaskDescription());
            taskEntity.setTaskLastModifiedBy(protoTask.getTaskLastModifiedBy());
            taskEntity.setTaskLastModifiedTimestamp(convertTimestamp(protoTask.getTaskLastModifiedTimeStamp()));

            // 4. Handle the specific payload type
            if (protoTask.getPayloadCase() == com.shared.protos.Task.PayloadCase.PRIORITY_FLOW_PAYLOAD) {
                var protoPayload = protoTask.getPriorityFlowPayload();

                // Create the specific PriorityFlowPayloadEntity
                PriorityFlowPayloadEntity payloadEntity = new PriorityFlowPayloadEntity();
                payloadEntity.setPrioFId(protoPayload.getPrioFId());
                payloadEntity.setPrioFRank(protoPayload.getPrioFRank());
                payloadEntity.setPrioFName(protoPayload.getPrioFName());

                // Establish bidirectional relationship for this payload
                taskEntity.setPriorityFlowPayload(payloadEntity);
            }
            else if(protoTask.getPayloadCase() == Task.PayloadCase.DATA_POPULATION_PAYLOAD){
                var protoPayload = protoTask.getDataPopulationPayload();

                DataPopulationPayloadEntity payloadEntity = new DataPopulationPayloadEntity();
                payloadEntity.setSourceSystem(protoPayload.getSourceSystem());
                payloadEntity.setTargetEntity(protoPayload.getTargetEntity());
                payloadEntity.setFilterCriteria(protoPayload.getFilterCriteria());
                payloadEntity.setPayloadData(payloadEntity.getPayloadData());

                taskEntity.setDataPopulationPayloadEntity(payloadEntity);
            }


            // 5. Establish relationship to the parent job and add to list
            taskEntities.add(taskEntity);
        }

        // 6. Set the collection of tasks on the job entity (helper method handles both sides)
        taskEntities.forEach(jobEntity::addTask);

        // 7. Save the aggregate root (JobEntity). JPA's cascading will persist all child entities.
        jobRepository.save(jobEntity);
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
