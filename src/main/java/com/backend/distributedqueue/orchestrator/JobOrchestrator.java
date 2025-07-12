package com.backend.distributedqueue.orchestrator;

import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.factory.JobProcessor;
import com.backend.distributedqueue.factory.JobProcessorFactory;
import com.backend.distributedqueue.producer.KafkaJobProducer;
import com.shared.protos.Job;
import com.shared.protos.JobAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Component
public class JobOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(JobOrchestrator.class);

    private final JobProcessorFactory jobProcessorFactory;

    @Autowired
    public KafkaJobProducer kafkaJobProducer;

    @Autowired
    public JobOrchestrator(JobProcessorFactory jobProcessorFactory) {
        this.jobProcessorFactory = jobProcessorFactory;
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

        try {
            performJobAction(job, job.getJobAction());
            logger.info("Successfully processed action {} for Job ID: {}", job.getJobAction(), job.getJobId());
        } catch (JobActivityException e) {
            logger.error("JobActivityException while processing action {} for Job ID {}: {}", job.getJobAction(), job.getJobId(), e.getMessage(), e);
            Job updatedJob = job.toBuilder().setJobAction(JobAction.JOB_FAILURE).setJobDescription(e.getMessage()).build();
            kafkaJobProducer.publishJobStatusUpdate(updatedJob);
        } catch (Exception e) {
            logger.error("Unexpected exception while processing action {} for Job ID {}: {}", job.getJobAction(), job.getJobId(), e.getMessage(), e);
            Job updatedJob = job.toBuilder().setJobAction(JobAction.JOB_FAILURE).build();
            kafkaJobProducer.publishJobStatusUpdate(updatedJob);
        }
    }


    /**
     * Performs the specified action (CREATE, UPDATE, DELETE) on the job
     * using the appropriate processor. This method is now typically called
     * by the Kafka listener.
     *
     * @param job    The job object.
     * @param action The action to perform.
     */
    public void performJobAction(Job job, JobAction action) {
        // Validate that the job has a payload case before getting a processor
        if (job.getPayloadCase() == Job.PayloadCase.PAYLOAD_NOT_SET) {
            throw new JobActivityException("Job payload is not set for Job ID: " + job.getJobId());
        }
        JobProcessor processor = jobProcessorFactory.getProcessor(job.getPayloadCase());
        Job updatedJob = switch (action) {
            case JOB_NEW -> processor.createJob(job);
            case JOB_UPDATE -> processor.updateJob(job);
            case JOB_DELETE -> processor.deleteJob(job);
            default ->
                    throw new JobActivityException("Unsupported job action: " + action + " for Job ID: " + job.getJobId());
        };

        // After performing the action, publish the updated job state and persist it.
        // The 'job' object here should reflect any changes made by the processor.
        kafkaJobProducer.publishJobStatusUpdate(updatedJob);
    }
}