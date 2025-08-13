package com.backend.distributedqueue.datapopulationjob.models;

import com.backend.distributedqueue.models.TaskEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Represents the payload for a data population task, stored in the 'data_population_payloads' table.
 * This entity corresponds to the DataPopulationPayload message in the Protobuf schema.
 */
@Data // Generates getters, setters, toString, equals, and hashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "data_population_payloads")
public class DataPopulationPayloadEntity {
    @Id
    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "source_system", nullable = false)
    private String sourceSystem;

    @Column(name = "target_entity", nullable = false)
    private String targetEntity;

    @Column(name = "filter_criteria", columnDefinition = "TEXT")
    private String filterCriteria;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_data", columnDefinition = "jsonb")
    private String payloadData;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Maps the 'taskId' ID field to the parent's ID
    @JoinColumn(name = "task_id")
    private TaskEntity task;
}