package com.backend.distributedqueue.orchestrator;

import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.factory.TaskProcessor;
import com.backend.distributedqueue.factory.TaskProcessorFactory;
import com.backend.distributedqueue.prioflow.dao.PrioFlowDao;
import com.backend.distributedqueue.producer.KafkaJobProducer;
import com.shared.protos.Task;
import com.shared.protos.Job;
import com.shared.protos.JobAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Component
public class JobOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(JobOrchestrator.class);

    // All dependencies should be final and injected via the constructor for robustness.
    private final TaskProcessorFactory taskProcessorFactory;
    private final KafkaJobProducer kafkaJobProducer;
    private final PrioFlowDao prioFlowDao;

    // A single constructor for all required dependencies. @Autowired is not needed on constructors with Spring 4.3+.
    public JobOrchestrator(TaskProcessorFactory taskProcessorFactory, KafkaJobProducer kafkaJobProducer, PrioFlowDao prioFlowDao) {
        this.taskProcessorFactory = taskProcessorFactory;
        this.kafkaJobProducer = kafkaJobProducer;
        this.prioFlowDao = prioFlowDao;
    }

    /**
     * Kafka Listener that consumes job action events from the 'job-actions' topic.
     * This method acts as the entry point for processing job-related commands
     * within the orchestrator.
     * Assumes the 'Job' Protobuf message now includes a 'JobAction' field.
     * If not, a wrapper Protobuf message (e.g., JobActionEvent) would be needed
     * to encapsulate both the Job and the JobAction.
     *
     * @param job The Job object received from Kafka, which is expected to contain the JobAction.
     */
    @KafkaListener(
            topics = "${kafka.topic.job-actions}", // Listen to the job-actions topic
            groupId = "job-processor-group",
            containerFactory = "kafkaListenerContainerFactory" // Re-use the configured factory
    )
    public void listenForJobActions(Job job) {
        logger.info("Received job action event for Job ID: {} with action: {}", job.getJobId(), job.getJobAction());

        if (job.getTasksCount() == 0) {
            logger.warn("Job with ID {} received with no tasks. Nothing to process.", job.getJobId());
            return;
        }

        List<Task> processedTasks = new ArrayList<>();
        try {
            for (Task task : job.getTasksList()) {
                TaskProcessor processor = taskProcessorFactory.getProcessor(task.getPayloadCase());
                Task processedTask = processor.process(task, job.getJobId(), job.getCreatedBy());
                processedTasks.add(processedTask);
            }
            // Build a final job status update message
            Job.Builder updatedJobStatus = job.toBuilder().clearTasks().addAllTasks(processedTasks);
            prioFlowDao.saveJob(updatedJobStatus.build());
            kafkaJobProducer.publishJobStatusUpdate(updatedJobStatus.build());
        } catch (Exception e) {
            logger.error("Unrecoverable exception in orchestrator for Job ID {}: {}", job.getJobId(), e.getMessage(), e);
            Job failureJob = job.toBuilder().setJobAction(JobAction.JOB_FAILURE).setJobDescription("Orchestrator failure: " + e.getMessage()).build();
            kafkaJobProducer.publishJobStatusUpdate(failureJob);
        }
    }
}