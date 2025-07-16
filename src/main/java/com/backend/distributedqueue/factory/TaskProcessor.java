package com.backend.distributedqueue.factory;

import com.shared.protos.Task;

/**
 * Defines the contract for a processor that can handle a specific type of task.
 * Implementations of this interface are responsible for the business logic
 * associated with a single task payload (e.g., sending an email, running a data flow).
 */
public interface TaskProcessor {

    /**
     * Processes a single task and returns the updated task with a new status.
     * @param task The task to be processed.
     * @return The updated task, typically with its status and description modified.
     */
    Task process(Task task, String jobId, String createdBY);

    /**
     * Specifies which task payload type this processor supports. This is used by the
     * factory to select the correct processor for a given task.
     * @return The supported {@link Task.PayloadCase}.
     */
    Task.PayloadCase getSupportedPayloadCase();
}