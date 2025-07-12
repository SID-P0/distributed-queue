package com.backend.distributedqueue.factory;

import com.shared.protos.Job;
import com.shared.protos.Job.PayloadCase;

/**
 * Defines a contract for processors that handle specific task actions within a Job.
 *
 * This interface is a direct parallel to the {@link com.backend.distributedqueue.factory.JobProcessor}.
 * While a JobProcessor handles high-level actions like JOB_NEW or JOB_UPDATE, a TaskProcessor
 * handles the granular, business-specific actions within the job's payload, such as
 * TASK_CREATE, TASK_SEND, etc.
 *
 * A concrete implementation (e.g., an EmailTaskProcessor) would implement the specific
 * methods it supports, like `sendTask`, and provide its associated payload type.
 */
public interface TaskProcessor {

    /**
     * Processes a task with the TASK_CREATE action.
     *
     * @param job The job containing the task to be created.
     * @return The job with its state updated after task execution.
     */
    default Job createTask(Job job) {
        throw new UnsupportedOperationException("Task Action 'CREATE' is not supported by this processor for payload: " + getSupportedPayloadCase());
    }

    /**
     * Processes a task with the TASK_UPDATE action.
     *
     * @param job The job containing the task to be updated.
     * @return The job with its state updated after task execution.
     */
    default Job updateTask(Job job) {
        throw new UnsupportedOperationException("Task Action 'UPDATE' is not supported by this processor for payload: " + getSupportedPayloadCase());
    }

    /**
     * Processes a task with the TASK_DELETE action.
     *
     * @param job The job containing the task to be deleted.
     * @return The job with its state updated after task execution.
     */
    default Job deleteTask(Job job) {
        throw new UnsupportedOperationException("Task Action 'DELETE' is not supported by this processor for payload: " + getSupportedPayloadCase());
    }

    /**
     * Processes a task with the TASK_SEND action.
     *
     * @param job The job containing the task to be sent.
     * @return The job with its state updated after task execution.
     */
    default Job sendTask(Job job) {
        throw new UnsupportedOperationException("Task Action 'SEND' is not supported by this processor for payload: " + getSupportedPayloadCase());
    }

    /**
     * Processes a task with the TASK_RECEIVE action.
     *
     * @param job The job containing the task to be received.
     * @return The job with its state updated after task execution.
     */
    default Job receiveTask(Job job) {
        throw new UnsupportedOperationException("Task Action 'RECEIVE' is not supported by this processor for payload: " + getSupportedPayloadCase());
    }


    /**
     * Returns the type of Job payload this task processor is associated with.
     * This is critical for a factory to route a job to the correct processor.
     * For example, an EmailTaskProcessor would return `Job.PayloadCase.EMAIL_PAYLOAD`.
     *
     * @return The specific payload case this processor handles.
     */
    PayloadCase getSupportedPayloadCase();
}
