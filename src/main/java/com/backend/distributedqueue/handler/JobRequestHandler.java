package com.backend.distributedqueue.handler;

import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.exception.TaskActivityException;
import com.backend.distributedqueue.orchestrator.JobOrchestrator;
import com.shared.protos.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobRequestHandler {
    @Autowired
    private JobOrchestrator jobOrchestrator;

    public void handleJob(Job job) {
        try {
            validateJob(job);
            jobOrchestrator.createJob(job);
        }
        catch (JobActivityException jobActivityException) {
            throw jobActivityException;
        }
        catch (TaskActivityException taskActivityException) {
            throw taskActivityException;
        }
        catch (Exception exception) {
            throw new RuntimeException("Error processing job " + job.getJobId() + ": " + exception.getMessage(), exception);
        }
    }

    public void validateJob(Job job) {
        if (job == null) {
            throw new JobActivityException("Job cannot be null.");
        }
    }
}
