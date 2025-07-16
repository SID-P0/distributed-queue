//package com.backend.distributedqueue.prioflow;
//
//import com.google.protobuf.Timestamp;
//import com.shared.protos.Job;
//import com.shared.protos.JobAction;
//import com.shared.protos.PriorityFlowPayload;
//import com.shared.protos.TaskAction;
//
//import java.time.Instant;
//import java.util.UUID;
//
//public class PrioFlowMessageBuilder {
//
//    /**
//     * Builds a Job protobuf message for a new Priority Flow task.
//     * This method populates the Job, PriorityFlowPayload, and TaskMetadata with the provided details.
//     *
//     * @param jobId        ID assigned to the job.
//     * @param createdBy    The identifier of the user or system creating the job.
//     * @param showName     The name of the priority flow.
//     * @return A fully constructed Job message ready to be sent.
//     */
//    public static Job buildNewPriorityFlowJob(String jobId, String createdBy, String showName) {
//
//        // Create a reusable timestamp for creation and modification times
//        Instant now = Instant.now();
//        Timestamp timestamp = Timestamp.newBuilder()
//                .setSeconds(now.getEpochSecond())
//                .setNanos(now.getNano())
//                .build();
//
//        // 1. Build the innermost TaskMetadata
//        TaskMetadata taskMetadata = TaskMetadata.newBuilder()
//                .setTaskId(UUID.randomUUID().toString()) // Assign a unique ID for the task
//                .setTaskAction(TaskAction.TASK_CREATE)   // Set the action for the task
//                .setTaskDescription("Task for processing priority flow: " + showName)
//                .setTaskLastModifiedBy(createdBy)
//                .setTaskLastModifiedTimeStamp(timestamp)
//                .build();
//
//        // 2. Build the specific PriorityFlowPayload, including the task metadata
//        PriorityFlowPayload priorityFlowPayload = PriorityFlowPayload.newBuilder()
//                .setTaskMetadata(taskMetadata)
//                .setPrioFId("")
//                .setPrioFName(showName)
//                .build();
//
//        // 3. Build the main Job message
//        Job job = Job.newBuilder()
//                .setJobId(jobId) // Assign a unique ID for the job
//                .setJobName(JobType.PRIORITY_FLOW_JOB.toString())
//                .setJobAction(JobAction.JOB_NEW) // Set the action for the job
//                .setJobType(JobType.PRIORITY_FLOW_JOB)
//                .setJobDescription("Job for processing priority flow: " + showName)
//                .setCreatedBy(createdBy)
//                .setCreationTimeStamp(timestamp)
//                .setPriorityFlowPayload(priorityFlowPayload) // Set the 'oneof' payload field
//                .build();
//
//        return job;
//    }
//
//    public static Job updatePrioFlowJob(String jobId, String createdBy, String showName) {
//
//        // Assign a unique ID for the job
//        // CRITICAL: Set the discriminator for the 'oneof'
//        // Set the 'oneof' payload field
//
//        return Job.newBuilder()
//                .setJobId(jobId) // Assign a unique ID for the job
//                .setJobName(JobType.PRIORITY_FLOW_JOB.toString())
//                .setJobType(JobType.PRIORITY_FLOW_JOB) // CRITICAL: Set the discriminator for the 'oneof'
//                .setJobDescription("Job for processing priority flow: " + showName)
//                .setCreatedBy(createdBy)
//                .setCreationTimeStamp(timestamp)
//                .setPriorityFlowPayload(priorityFlowPayload) // Set the 'oneof' payload field
//                .build();
//    }
//}
