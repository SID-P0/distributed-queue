package com.backend.distributedqueue.prioflow.dto;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents the specific data for a priority flow task, mapping to the
 * 'priority_flow_payloads' table.
 */
@Entity
@Table(name = "priority_flow_payloads")
@Data
@NoArgsConstructor
public class PriorityFlowPayloadEntity {

    @Id
    @Column(name = "task_id")
    private UUID taskId;

    /**
     * Establishes a one-to-one relationship where the primary key of this entity
     * is also a foreign key to the TaskEntity.
     * '@MapsId' is used to indicate that the primary key is derived from the
     * 'task' relationship.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @Column(name = "prio_f_id", nullable = false)
    private String prioFId;

    @Column(name = "prio_f_rank", nullable = false)
    private int prioFRank;

    @Column(name = "prio_f_name", nullable = false)
    private String prioFName;
}
