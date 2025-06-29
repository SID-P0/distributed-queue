package com.backend.distributedqueue.prioflow;

import com.backend.distributedqueue.factory.JobProcessor;
import com.shared.protos.Job;

public class PriorityFlowProcessor implements JobProcessor {
    @Override
    public void createJob(Job job) {

    }

    @Override
    public Job.PayloadCase getSupportedPayloadCase() {
        return Job.PayloadCase.PRIORITY_FLOW_PAYLOAD;
    }
}
