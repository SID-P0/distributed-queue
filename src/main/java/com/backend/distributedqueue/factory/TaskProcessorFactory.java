package com.backend.distributedqueue.factory;

import com.backend.distributedqueue.exception.JobActivityException;
import com.shared.protos.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A factory that provides the correct {@link TaskProcessor} for a given task.
 * It maintains a cache of all available task processors, keyed by the payload
 * type they support.
 */
@Component
public class TaskProcessorFactory {
    private final Map<Task.PayloadCase, TaskProcessor> processorsCache;

    @Autowired
    public TaskProcessorFactory(List<TaskProcessor> processors) {
        this.processorsCache = processors.stream()
                .collect(Collectors.toMap(TaskProcessor::getSupportedPayloadCase, Function.identity()));
    }

    public TaskProcessor getProcessor(Task.PayloadCase payloadCase) {
        TaskProcessor processor = processorsCache.get(payloadCase);
        if (processor == null) {
            throw new JobActivityException("No processor found for payload case: " + payloadCase);
        }
        return processor;
    }
}
