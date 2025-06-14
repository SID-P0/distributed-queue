package com.backend.distributedqueue.orchestrator;

import com.backend.distributedqueue.exception.JobActivityException;
import com.shared.protos.DataPopulationPayload;
import com.shared.protos.EmailPayload;
import com.shared.protos.Job;
import org.springframework.stereotype.Component;

@Component
public class JobOrchestrator {

    public void processJob(Job job) {
        // Determine the job type and delegate to the appropriate handler/processor
        switch (job.getJobType()) {
            case EMAIL_JOB:
                processEmailJob(job.getEmailPayload());
                break;
            case DATA_POPULATION_JOB:
                processDataPopulationJob(job.getDataPopulationPayload());
                break;
            case PRIORITY_FLOW_JOB:
                processPriorityFlowJob(job.getPriorityFlowPayload());
                break;
            case JOB_TYPE_UNSPECIFIED:
            default:
                // Handle unknown or unspecified job types
                throw new JobActivityException("Unknown or unspecified job type");
        }
    }

    private void processEmailJob(EmailPayload emailPayload) {
        System.out.println("Processing Email Job: " + emailPayload.getUniqueMailId());
        // Add your email processing logic here
    }

    private void processDataPopulationJob(DataPopulationPayload dataPopulationPayload) {
        System.out.println("Processing Data Population Job for entity: " + dataPopulationPayload.getTargetEntity());
        // Add your data population logic here
    }

    private void processPriorityFlowJob(com.shared.protos.PriorityFlowPayload priorityFlowPayload) {
        System.out.println("Processing Priority Flow Job: " + priorityFlowPayload.getPrioFName());
        // Add your priority flow logic here
    }

}
