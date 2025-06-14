package com.backend.distributedqueue.emailjob;

import com.backend.distributedqueue.factory.JobProcessor;
import com.shared.protos.EmailPayload;
import com.shared.protos.Job;
import org.springframework.stereotype.Component;

@Component
public class EmailProcessor implements JobProcessor {

    @Override
    public void createJob(Job job) {
        EmailPayload emailPayload = job.getEmailPayload();
        System.out.println("Processing Email Job via Factory: " + emailPayload.getUniqueMailId());
    }

    @Override
    public void updateJob(Job job) {
        EmailPayload emailPayload = job.getEmailPayload();
        System.out.println("Updating Email Job via Factory: " + emailPayload.getUniqueMailId());
    }

    @Override
    public void deleteJob(Job job) {
        EmailPayload emailPayload = job.getEmailPayload();
        System.out.println("Deleting Email Job via Factory: " + emailPayload.getUniqueMailId());
    }

    @Override
    public Job.PayloadCase getSupportedPayloadCase() {
        return Job.PayloadCase.EMAIL_PAYLOAD; // Adjust based on your 'oneof' name and generated enum
    }
}
