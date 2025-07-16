package com.backend.distributedqueue.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class JobEntity {

    @Id
    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "job_action", nullable = false)
    private String jobAction;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "creation_timestamp", nullable = false)
    private OffsetDateTime creationTimestamp;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TaskEntity> tasks = new ArrayList<>();

    /**
     * Helper method to maintain the bidirectional relationship between Job and Task.
     */
    public void addTask(TaskEntity task) {
        tasks.add(task);
        task.setJob(this);
    }
}