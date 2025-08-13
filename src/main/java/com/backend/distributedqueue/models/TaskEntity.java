package com.backend.distributedqueue.models;

import com.backend.distributedqueue.datapopulationjob.models.DataPopulationPayloadEntity;
import com.backend.distributedqueue.prioflow.models.PriorityFlowPayloadEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class TaskEntity {

    @Id
    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "task_type", nullable = false)
    private String taskType; // Discriminator: e.g., "PRIORITY_FLOW_PAYLOAD"

    @Column(name = "task_action", nullable = false)
    private String taskAction;

    @Column(name = "task_description")
    private String taskDescription;

    @Column(name = "task_last_modified_by", nullable = false)
    private String taskLastModifiedBy;

    @Column(name = "task_last_modified_timestamp", nullable = false)
    private OffsetDateTime taskLastModifiedTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", referencedColumnName = "job_id", nullable = false)
    private JobEntity job;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private PriorityFlowPayloadEntity priorityFlowPayload;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private DataPopulationPayloadEntity dataPopulationPayloadEntity;

    /**
     * Helper method to maintain the bidirectional relationship for the payload.
     */
    public void setPriorityFlowPayload(PriorityFlowPayloadEntity payload) {
        if (payload != null) {
            this.priorityFlowPayload = payload;
            payload.setTask(this);
        }
    }
}