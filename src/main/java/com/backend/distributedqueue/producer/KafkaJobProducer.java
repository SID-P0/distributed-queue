package com.backend.distributedqueue.producer;

import com.shared.protos.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import Value for injecting properties
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate; // Import KafkaTemplate for sending messages
import org.springframework.stereotype.Component;

@Component
public class KafkaJobProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaJobProducer.class);

    @Autowired
    private KafkaTemplate<String, Job> kafkaTemplate;

    @Value("${kafka.topic.job-actions}")
    private String jobActionsTopic;

    @Value("${kafka.topic.job-status-updates}")
    private String jobStatusUpdates;

    /**
     * This listener belongs to the 'job-processor-group'.
     * It acts as an entry point for incoming job requests from UI/Cron Jobs.
     */
    public void publishingJobRequestsForProcessing(Job job) {
        logger.info("Received Job request : {}", job);
        try {
            logger.info("Publishing event with Job ID {} to topic {}", job.getJobId(), jobActionsTopic);
            kafkaTemplate.send(jobActionsTopic, job.getJobId(), job);
            logger.info("Successfully published new job event with ID : {}", job.getJobId());
        } catch (Exception e) {
            logger.error("Failed to publish job {} due to an exception : ", job.getJobId(), e);
            //TODO Push to retry queue/DLQ
        }
    }

    /**
     * This listener belongs to the 'job-status-update-group'.
     * It acts as an entry point for processed job requests and sends the business enriched response back to UI/Cron Jobs.
     */
    public void publishingJobResponsesForSSE(Job job) {
        logger.info("Received Job request to publish as a SSE : {}", job);
        try {
            logger.info("Publishing event with Job ID {} to topic {}", job.getJobId(), jobStatusUpdates);
            kafkaTemplate.send(jobStatusUpdates, job.getJobId(), job);
            logger.info("Successfully published new job event with ID : {}", job.getJobId());
        } catch (Exception e) {
            logger.error("Failed to publish job {} due to an exception : ", job.getJobId(), e);
            //TODO Push to retry queue/DLQ
        }
    }

}