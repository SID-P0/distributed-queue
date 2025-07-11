package com.backend.distributedqueue.datapopulationjob;

import com.backend.distributedqueue.factory.JobProcessor;
import com.shared.protos.DataPopulationPayload;
import com.shared.protos.Job;
import org.springframework.stereotype.Component;

/**
 * TODO: This processor should be its own separate external app in itself within this monorepo in order to scale this functionality.
 */
@Component
public class DataPopulationProcessor implements JobProcessor {

    @Override
    public Job createJob(Job job) {
        DataPopulationPayload dataPopulationPayload = job.getDataPopulationPayload();
        System.out.println("Processing DataPopulation Job via Factory: " + dataPopulationPayload.getSourceSystem());
        return job;
    }

    @Override
    public Job.PayloadCase getSupportedPayloadCase() {
        return Job.PayloadCase.DATA_POPULATION_PAYLOAD;
    }
}
