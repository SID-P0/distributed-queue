package com.backend.distributedqueue.handler;

import com.backend.distributedqueue.orchestrator.JobOrchestrator;
import com.shared.protos.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobRequestHandler {
    @Autowired
    private JobOrchestrator jobOrchestrator;

    public void handleJob(Job job) {

    }
}
