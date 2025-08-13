package com.backend.distributedqueue.prioflow;

import com.backend.distributedqueue.exception.JobActivityException;
import com.backend.distributedqueue.factory.TaskProcessor;
import com.backend.distributedqueue.prioflow.service.PrioFlowService;
import com.shared.protos.Task;
import com.shared.protos.TaskAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO: This processor should be its own separate external app in itself within this monorepo in order to scale this functionality.
 * <p>
 * This will act like bookmyshow/ticketmaster for concerts/movie booking purpose.
 * The main purpose of this would be to poll the kafka, For example we if want only first 100 people which queued to be able to perform further actions.
 * These polled out users will be given session based chance's to complete their desired action, We can use redis to maintain these sessions.
 */
@Component
public class PriorityFlowProcessor implements TaskProcessor {

    private final PrioFlowService prioFlowService;

    @Autowired
    public PriorityFlowProcessor(PrioFlowService prioFlowService) {
        this.prioFlowService = prioFlowService;
    }

    @Override
    public Task process(Task task, String jobId, String createdBy) {
        try {
            if (!task.hasPriorityFlowPayload()) {
                throw new JobActivityException("Task is missing PriorityFlowPayload.");
            }

            // Delegate processing to the service based on the task action.
            Task processedTask = switch (task.getTaskAction()) {
                case TASK_CREATE -> prioFlowService.createTask(task, jobId, createdBy);
                case TASK_UPDATE -> prioFlowService.updateTask(task, jobId, createdBy);
                case TASK_DELETE -> throw new JobActivityException(
                        "Action " + task.getTaskAction() + " is not yet implemented for PriorityFlowProcessor.");
                default -> throw new JobActivityException(
                        "Unsupported action for PriorityFlowProcessor: " + task.getTaskAction());
            };

            // If the service call completes without an exception, we consider it a success.
            // The final task state is set to TASK_SUCCESS.
            return processedTask.toBuilder().setTaskAction(TaskAction.TASK_SUCCESS).build();
        } catch (Exception e) {
            if (e instanceof JobActivityException) {
                throw e; // Re-throw exceptions we've already wrapped.
            }
            throw new JobActivityException("Failed to process PriorityFlow task: " + e.getMessage(), e);
        }
    }

    @Override
    public Task.PayloadCase getSupportedPayloadCase() {
        return Task.PayloadCase.PRIORITY_FLOW_PAYLOAD;
    }
}
