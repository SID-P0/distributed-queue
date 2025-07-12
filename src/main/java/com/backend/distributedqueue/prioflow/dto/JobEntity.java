package com.backend.distributedqueue.prioflow.dto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
/**
 * Represents a high-level job container, mapping to the 'jobs' table.
 * This is the aggregate root for a job and its associated tasks.
 */
@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
public class JobEntity {

    @Id
    @Column(name = "job_id")
    private UUID jobId;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "job_action", nullable = false, length = 50)
    private String jobAction;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "creation_timestamp", nullable = false, updatable = false)
    private OffsetDateTime creationTimestamp;

    /**
     * A job can have one or more tasks.
     * 'mappedBy = "job"' indicates that the TaskEntity is the owner of the relationship.
     * 'cascade = CascadeType.ALL' ensures that when a JobEntity is saved, its child
     * TaskEntities are also saved. When a JobEntity is deleted, its tasks are deleted too.
     */
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TaskEntity> tasks;
}
