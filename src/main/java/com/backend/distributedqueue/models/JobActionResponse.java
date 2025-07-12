package com.backend.distributedqueue.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobActionResponse {
    private String message;
    private String jobId;
}
