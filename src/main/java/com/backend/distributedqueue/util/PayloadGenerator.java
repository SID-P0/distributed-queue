package com.backend.distributedqueue.util;

import com.google.protobuf.Timestamp;
import com.shared.protos.Job;
import com.shared.protos.JobAction;
import com.shared.protos.PriorityFlowPayload;
import com.shared.protos.Task;
import com.shared.protos.TaskAction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A utility to convert a human-readable Protobuf text file into its binary format.
 * This is a replacement for the 'protoc --encode' command-line tool.
 */
public class PayloadGenerator {

    /**
     * Reads 'testJob.txt', converts it to binary, and saves it as 'job.bin'.
     *
     * @param args Command line arguments (not used).
     * @throws IOException if there is an error reading the input file or writing the output file.
     */
    public static void main(String[] args) throws IOException {
        // A client sends a task specifying its type via an empty payload.
        // The backend is responsible for populating the payload's fields.
        Task priorityTask = Task.newBuilder()
                .setTaskId("")
                .setTaskAction(TaskAction.TASK_CREATE)
                // Setting an empty payload correctly sets the 'payload' oneof case,
                // allowing the orchestrator to route it to the correct processor.
                .setPriorityFlowPayload(PriorityFlowPayload.newBuilder().build())
                .build();

        // The Job itself is also minimal, containing only the action and originator.
        // The backend will set the job_id, name, description, and timestamps.
        Job job = Job.newBuilder()
                .setJobAction(JobAction.JOB_NEW)
                .setCreatedBy("sidheshs")
                .addTasks(priorityTask) // Add the task to the job
                .build();

        Path output = Paths.get("job.bin");
        Files.write(output, job.toByteArray());

        System.out.println("✅ job.bin written successfully.");
    }
}