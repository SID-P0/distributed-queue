package com.backend.distributedqueue.producer;

import com.shared.protos.Job;
import lombok.RequiredArgsConstructor; // Import for constructor injection
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // Use Lombok to create the constructor for you
public class KafkaJobProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaJobProducer.class);

    // Dependencies are now final and injected via the constructor
    private final KafkaTemplate<String, Job> kafkaTemplate;

    @Value("${kafka.topic.job-actions}")
    private String jobActionsTopic;

    @Value("${kafka.topic.job-status-updates}")
    private String jobStatusUpdatesTopic; // Renamed for clarity

    /**
     * Publishes a new or updated job to the processing topic.
     * @param job The job object enriched by the backend.
     */
    public void publishJobForProcessing(Job job) {
        logger.info("Preparing to publish job with ID {} to topic '{}'", job.getJobId(), jobActionsTopic);
        try {
            // The key is the job_id, which ensures all events for the same job go to the same partition.
            kafkaTemplate.send(jobActionsTopic, job.getJobId(), job);
            logger.info("Successfully published job event for ID: {}", job.getJobId());
        } catch (Exception e) {
            // CORRECTED LOGGING: The exception 'e' is the last argument.
            logger.error("Failed to publish job with ID {} to topic '{}'", job.getJobId(), jobActionsTopic, e);
            // TODO: Implement a robust retry/DLQ (Dead Letter Queue) strategy here.
        }
    }

    /**
     * Publishes the final status of a processed job for consumers like SSE emitters.
     * @param job The processed job with its final status.
     */
    public void publishJobStatusUpdate(Job job) {
        logger.info("Preparing to publish status update for job ID {} to topic '{}'", job.getJobId(), jobStatusUpdatesTopic);
        try {
            kafkaTemplate.send(jobStatusUpdatesTopic, job.getJobId(), job);
            logger.info("Successfully published status update for job ID: {}", job.getJobId());
        } catch (Exception e) {
            // CORRECTED LOGGING
            logger.error("Failed to publish status update for job ID {} to topic '{}'", job.getJobId(), jobStatusUpdatesTopic, e);
            // TODO: Implement a robust retry/DLQ strategy here.
        }
    }
}