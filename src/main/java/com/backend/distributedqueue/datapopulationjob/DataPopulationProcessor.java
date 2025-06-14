package com.backend.distributedqueue.datapopulationjob;

import com.backend.distributedqueue.factory.JobProcessor;
import com.shared.protos.DataPopulationPayload;
import com.shared.protos.Job;
import org.springframework.stereotype.Component;

@Component
public class DataPopulationProcessor implements JobProcessor {

    @Override
    public void createJob(Job job) {
        DataPopulationPayload dataPopulationPayload = job.getDataPopulationPayload();
        System.out.println("Processing DataPopulation Job via Factory: " + dataPopulationPayload.getSourceSystem());
    }

    @Override
    public Job.PayloadCase getSupportedPayloadCase() {
        return Job.PayloadCase.DATA_POPULATION_PAYLOAD;
    }
}
