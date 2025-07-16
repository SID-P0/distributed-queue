package com.backend.distributedqueue.prioflow.dto;

import com.backend.distributedqueue.models.TaskEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "priority_flow_payloads")
@Getter
@Setter
public class PriorityFlowPayloadEntity {

    @Id
    @Column(name = "task_id")
    private UUID taskId;

    @Column(name = "prio_f_id", nullable = false)
    private String prioFId;

    @Column(name = "prio_f_rank", nullable = false)
    private Integer prioFRank;

    @Column(name = "prio_f_name", nullable = false)
    private String prioFName;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Maps the 'taskId' ID field to the parent's ID
    @JoinColumn(name = "task_id")
    private TaskEntity task;
}