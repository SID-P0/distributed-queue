package com.backend.distributedqueue.util;

import com.google.protobuf.TextFormat;
import com.shared.protos.Job;
import com.shared.protos.JobAction;
import com.shared.protos.JobType;
import com.shared.protos.PriorityFlowPayload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        Job job = Job.newBuilder()
                .setJobName("PrioFlow test")
                .setJobAction(JobAction.JOB_NEW)
                .setJobType(JobType.PRIORITY_FLOW_JOB)
                .setCreatedBy("sidheshs")
                .setPriorityFlowPayload(PriorityFlowPayload.newBuilder()
                        .setPrioFId("PF-MARKETING-Q4-2024")
                        .setPrioFRank(1)
                        .setPrioFName("Q4-Marketing-Campaign-Flow")
                        .build())
                .build();

        Path output = Paths.get("job.bin");
        Files.write(output, job.toByteArray());

        System.out.println("✅ job.bin written successfully.");
    }
}