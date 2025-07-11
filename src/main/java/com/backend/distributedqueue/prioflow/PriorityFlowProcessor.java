package com.backend.distributedqueue.prioflow;

import com.backend.distributedqueue.factory.JobProcessor;
import com.backend.distributedqueue.prioflow.service.PrioFlowService;
import com.shared.protos.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO: This processor should be its own separate external app in itself within this monorepo in order to scale this functionality.

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
        return job;
    }

    @Override
    public Job.PayloadCase getSupportedPayloadCase() {
        return Job.PayloadCase.PRIORITY_FLOW_PAYLOAD;
    }
}
