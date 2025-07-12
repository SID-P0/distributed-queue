package com.backend.distributedqueue.prioflow.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a single task within a job, mapping to the 'tasks' table.
 * It contains common metadata and links to a specific payload entity.
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class TaskEntity {

    @Id
    @Column(name = "task_id")
    private UUID taskId;

    /**
     * The owning side of the many-to-one relationship with JobEntity.
     * This column cannot be null, as every task must belong to a job.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobEntity job;

    @Column(name = "task_type", nullable = false, length = 50)
    private String taskType;

    @Column(name = "task_action", nullable = false, length = 50)
    private String taskAction;

    @Column(name = "task_description")
    private String taskDescription;

    @Column(name = "task_last_modified_by", nullable = false)
    private String taskLastModifiedBy;

    @Column(name = "task_last_modified_timestamp", nullable = false)
    private OffsetDateTime taskLastModifiedTimestamp;

    /**
     * A task has one specific payload. This establishes the one-to-one relationship.
     * 'cascade = CascadeType.ALL' ensures the payload is saved/deleted along with the task.
     */
    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private PriorityFlowPayloadEntity priorityFlowPayload;

    // Note: You would add other @OneToOne relationships here for EmailPayloadEntity, etc.
}
