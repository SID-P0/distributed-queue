package com.backend.distributedqueue.validation;

import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.producer.KafkaJobProducer;
import com.shared.protos.Job;
import com.shared.protos.JobAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Its role is to validate that the required job fields are populated correctly before publishing to the event queue
 */
@Component
public class JobRequestValidation {

    @Autowired
    KafkaJobProducer kafkaJobProducer;

    public void validateAndPublishJob(Job job, JobAction jobAction) {
        try {
            // Assign a unique key when job arrives for the first time for kafka partitioning, Else process for further actions
            if (jobAction.equals(JobAction.JOB_NEW)) {
                job = assignUniqueJobKeyId(job);
            }
            validateJob(job);
            kafkaJobProducer.publishingJobRequestsForProcessing(job);
        }
        catch (JobActivityException jobActivityException) {
            throw new JobActivityException("Error processing job " + job.getJobId() + ": " + jobActivityException.getMessage(), jobActivityException);
        }
        catch (Exception exception) {
            throw new RuntimeException("Error processing job " + job.getJobId() + ": " + exception.getMessage(), exception);
        }
    }

    public Job assignUniqueJobKeyId(Job job) {
        return job.toBuilder().setJobId(UUID.randomUUID().toString()).build();
    }

    public void validateJob(Job job) {
        if (job == null || job.getJobId().isEmpty() || job.getJobAction() == JobAction.JOB_STATUS_UNSPECIFIED || job.getCreatedBy().isEmpty()) {
            throw new JobActivityException("Job creation was not successful, missing required fields : {}. \n Please contact the admin.", job);
        }
    }
}
