package com.backend.distributedqueue.emailjob;

import com.backend.distributedqueue.factory.JobProcessor;
import com.shared.protos.EmailPayload;
import com.shared.protos.Job;
import org.springframework.stereotype.Component;

/**
 * TODO: This processor should be its own separate external app in itself within this monorepo in order to scale this functionality.
 */
@Component
public class EmailProcessor implements JobProcessor {

    @Override
    public Job createJob(Job job) {
        EmailPayload emailPayload = job.getEmailPayload();
        System.out.println("Processing Email Job via Factory: " + emailPayload.getUniqueMailId());
        return job;
    }

    @Override
    public Job updateJob(Job job) {
        EmailPayload emailPayload = job.getEmailPayload();
        System.out.println("Updating Email Job via Factory: " + emailPayload.getUniqueMailId());
        return job;
    }

    @Override
    public Job deleteJob(Job job) {
        EmailPayload emailPayload = job.getEmailPayload();
        System.out.println("Deleting Email Job via Factory: " + emailPayload.getUniqueMailId());
        return job;
    }

    @Override
    public Job.PayloadCase getSupportedPayloadCase() {
        return Job.PayloadCase.EMAIL_PAYLOAD; // Adjust based on your 'oneof' name and generated enum
    }
}
