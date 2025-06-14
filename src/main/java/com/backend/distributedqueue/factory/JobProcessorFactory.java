package com.backend.distributedqueue.factory;

import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.factory.JobProcessor;
import com.shared.protos.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JobProcessorFactory {
    private final Map<Job.PayloadCase, JobProcessor> processorsCache;

    @Autowired
    public JobProcessorFactory(List<JobProcessor> processors) {
        this.processorsCache = processors.stream()
                .collect(Collectors.toMap(JobProcessor::getSupportedPayloadCase, Function.identity()));
    }

    public JobProcessor getProcessor(Job.PayloadCase payloadCase) {
        if (payloadCase == null || payloadCase == Job.PayloadCase.PAYLOAD_NOT_SET) { // Adjust enum value
            throw new JobActivityException("Job payload not set or unspecified.");
        }
        JobProcessor processor = processorsCache.get(payloadCase);
        if (processor == null) {
            throw new JobActivityException("No processor found for payload case: " + payloadCase);
        }
        return processor;
    }
}
