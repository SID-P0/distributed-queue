package com.backend.distributedqueue.datapopulationjob;

import com.backend.distributedqueue.factory.TaskProcessor;
import com.shared.protos.Task;
import org.springframework.stereotype.Component;

/**
 * TODO: This processor should be its own separate external app in itself within this monorepo in order to scale this functionality.
 */
@Component
public class DataPopulationProcessor implements TaskProcessor {

    public Task process(Task task, String jobId, String createdBy) {
        return null;
    }

    @Override
    public Task.PayloadCase getSupportedPayloadCase() {
        return Task.PayloadCase.DATA_POPULATION_PAYLOAD;
    }
}
