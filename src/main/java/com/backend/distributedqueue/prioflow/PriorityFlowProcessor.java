package com.backend.distributedqueue.prioflow;

import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.factory.JobProcessor;
import com.backend.distributedqueue.prioflow.service.PrioFlowService;
import com.shared.protos.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO: This processor should be its own separate external app in itself within this monorepo in order to scale this functionality.
 * <p>
 * This will act like bookmyshow/ticketmaster for concerts/movie booking purpose.
 * The main purpose of this would be to poll the kafka, For example we if want only first 100 people which queued to be able to perform further actions.
 * These polled out users will be given session based chance's to complete their desired action, We can use redis to maintain these sessions.
 */
@Component
public class PriorityFlowProcessor implements JobProcessor {

    @Autowired
    private PrioFlowService prioFlowService;

    @Override
    public Job createJob(Job job) {
        try {
            if (job.getJobId().isEmpty() || job.getCreatedBy().isEmpty()) {
                throw new JobActivityException("Missing required fields for to perform action on PriorityFlow job.");
            }
            return prioFlowService.createTask(PrioFlowMessageBuilder.buildNewPriorityFlowJob(job.getJobId(), job.getCreatedBy(), "ExampleShow"));
        } catch (Exception e) {
            throw new JobActivityException("Failed to create PriorityFlow job: " + e.getMessage(), e);
        }
    }

    @Override
    public Job.PayloadCase getSupportedPayloadCase() {
        return Job.PayloadCase.PRIORITY_FLOW_PAYLOAD;
    }
}
