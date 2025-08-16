package com.backend.distributedqueue.orchestrator;

import com.backend.distributedqueue.factory.TaskProcessor;
import com.backend.distributedqueue.factory.TaskProcessorFactory;
import com.backend.distributedqueue.dao.JobFlowDao;
import com.backend.distributedqueue.producer.KafkaJobProducer;
import com.backend.distributedqueue.sse.SSEService;
import com.shared.protos.Task;
import com.shared.protos.Job;
import com.shared.protos.JobAction;
import com.shared.protos.TaskAction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Orchestrates job processing by consuming job actions from Kafka.
 * It processes tasks within a job in parallel for improved throughput.
 */
@Component
public class JobOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(JobOrchestrator.class);

    private final TaskProcessorFactory taskProcessorFactory;
    private final KafkaJobProducer kafkaJobProducer;
    private final JobFlowDao jobFlowDao;
    private final SSEService sseService;
    private final ExecutorService taskExecutor;

    public JobOrchestrator(TaskProcessorFactory taskProcessorFactory,
                           KafkaJobProducer kafkaJobProducer,
                           JobFlowDao jobFlowDao,
                           SSEService sseService,
                           @Qualifier("taskExecutor") ExecutorService taskExecutor) {
        this.taskProcessorFactory = taskProcessorFactory;
        this.kafkaJobProducer = kafkaJobProducer;
        this.jobFlowDao = jobFlowDao;
        this.sseService = sseService;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Kafka Listener that consumes job action events from the 'job-actions' topic.
     * It processes all tasks for a given job in parallel using a dedicated thread pool.
     *
     * @param job The Job object received from Kafka.
     */
    @KafkaListener(
            topics = "${kafka.topic.job-actions}",
            groupId = "job-processor-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenForJobActions(Job job) {
        logger.info("Received job action event for Job ID: {} with action: {}", job.getJobId(), job.getJobAction());
        if (job.getTasksCount() == 0) {
            logger.warn("Job with ID {} received with no tasks. Nothing to process.", job.getJobId());
            return;
        }

        try {
            // 1. Create a CompletableFuture for each task.
            List<CompletableFuture<Task>> futureTasks = job.getTasksList().stream()
                    .map(task -> CompletableFuture.supplyAsync(() -> {
                                logger.info("Processing task {} for job {}", task.getTaskId(), job.getJobId());
                                TaskProcessor processor = taskProcessorFactory.getProcessor(task.getPayloadCase());
                                return processor.process(task, job.getJobId(), job.getCreatedBy());
                            }, taskExecutor)
                            // 2. Use .handle() to process success or failure for EACH task individually.
                            // This prevents one failure from stopping the entire result collection.
                            .handle((successResult, exception) -> {
                                if (exception != null) {
                                    logger.error("Task {} failed for Job ID {}: {}", task.getTaskId(), job.getJobId(), exception.getMessage());
                                    // On failure, return the original task but with a FAILED status.
                                    return task.toBuilder()
                                            .setTaskAction(TaskAction.TASK_FAILURE)
                                            .setTaskDescription("Task failed: " + exception.getMessage())
                                            .build();
                                }
                                // On success, return the processed task.
                                return successResult;
                            }))
                    .toList();

            // 2. Wait for all tasks to complete and collect the results
            List<Task> processedTasks = CompletableFuture.allOf(futureTasks.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futureTasks.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()))
                    .get();

            // 3. Build a final job status update message with the results
            Job updatedJobStatus = job.toBuilder()
                    .clearTasks()
                    .addAllTasks(processedTasks)
                    .build();

            jobFlowDao.saveJob(updatedJobStatus);
            kafkaJobProducer.publishJobStatusUpdate(updatedJobStatus);
            // Also broadcast the final status, not the initial one
            sseService.broadcastEvent(updatedJobStatus);

        } catch (Exception e) {
            // If any task fails, the whole job is marked as failed.
            logger.error("Unrecoverable exception in orchestrator for Job ID {}: {}", job.getJobId(), e.getMessage(), e);
            Job failureJob = job.toBuilder()
                    .setJobAction(JobAction.JOB_FAILURE)
                    .setJobDescription("Orchestrator failure: " + e.getMessage())
                    .build();
            jobFlowDao.saveJob(failureJob);
            kafkaJobProducer.publishJobStatusUpdate(failureJob);
            // Broadcast the failure status
            sseService.broadcastEvent(failureJob);
        }
    }
}