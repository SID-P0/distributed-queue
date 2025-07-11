package com.backend.distributedqueue.factory;

import com.shared.protos.Job;

public interface JobProcessor {
    /**
     * Processes the specific payload within the job.
     * @param job The job containing the payload to createJob.
     */
    Job createJob(Job job);

    default Job updateJob(Job job){
        throw new UnsupportedOperationException("Update operation is not supported for this job type: " + job.getPayloadCase());
    }

    default Job deleteJob(Job job){
        throw new UnsupportedOperationException("Delete operation is not supported for this job type: " + job.getPayloadCase());
    }

    /**
     * @return The specific payload case this processor handles.
     *         e.g., Job.PayloadTypeCase.EMAIL_PAYLOAD
     */
    Job.PayloadCase getSupportedPayloadCase();
}
