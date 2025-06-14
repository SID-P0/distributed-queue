package com.backend.distributedqueue.orchestrator;

import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.factory.JobProcessor;
import com.backend.distributedqueue.factory.JobProcessorFactory;
import com.shared.protos.DataPopulationPayload;
import com.shared.protos.EmailPayload;
import com.shared.protos.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobOrchestrator {

    private final JobProcessorFactory jobProcessorFactory;

    @Autowired
    public JobOrchestrator(JobProcessorFactory jobProcessorFactory) {
        this.jobProcessorFactory = jobProcessorFactory;
    }

    public void createJob(Job job) {
        //Fetch the payload type
        Job.PayloadCase payloadCase = job.getPayloadCase();
        // Create an object of the fetched payload
        JobProcessor processor = jobProcessorFactory.getProcessor(payloadCase);
        processor.createJob(job); // The processor handles extracting and processing its specific payload

        publishAndPersistJob(job);
    }

    public void updateJob(Job job) {
        //Fetch the payload type
        Job.PayloadCase payloadCase = job.getPayloadCase();
        // Create an object of the fetched payload
        JobProcessor processor = jobProcessorFactory.getProcessor(payloadCase);
        processor.updateJob(job); // The processor handles extracting and processing its specific payload

        publishAndPersistJob(job);
    }

    public void deleteJob(Job job) {
        //Fetch the payload type
        Job.PayloadCase payloadCase = job.getPayloadCase();
        // Create an object of the fetched payload
        JobProcessor processor = jobProcessorFactory.getProcessor(payloadCase);
        processor.deleteJob(job); // The processor handles extracting and processing its specific payload

        publishAndPersistJob(job);
    }

    public void publishAndPersistJob(Job job){
        
    }

}
