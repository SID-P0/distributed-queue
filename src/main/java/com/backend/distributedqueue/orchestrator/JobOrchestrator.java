package com.backend.distributedqueue.orchestrator;

import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.factory.JobProcessor;
import com.backend.distributedqueue.factory.JobProcessorFactory;
import com.shared.protos.Job;
import com.shared.protos.JobAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobOrchestrator {

    private final JobProcessorFactory jobProcessorFactory;


    @Autowired
    public JobOrchestrator(JobProcessorFactory jobProcessorFactory) {
        this.jobProcessorFactory = jobProcessorFactory;
    }
    /**
     * Performs the specified action (CREATE, UPDATE, DELETE) on the job
     * using the appropriate processor.
     *
     * @param job The job object.
     * @param action The action to perform.
     */
    public void performJobAction(Job job, JobAction action) {
        JobProcessor processor = jobProcessorFactory.getProcessor(job.getPayloadCase());

        switch (action) {
            case JobAction.JOB_NEW:
                processor.createJob(job);
                break;
            case JobAction.JOB_UPDATE:
                processor.updateJob(job);
                break;
            case JobAction.JOB_DELETE:
                processor.deleteJob(job);
                break;
            default:
                throw new JobActivityException("Unsupported job action: " + action);
        }
        publishAndPersistJob(job);
    }

    public void publishAndPersistJob(Job job) {

    }
}