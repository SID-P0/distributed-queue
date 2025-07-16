package com.backend.distributedqueue.prioflow.repository;

import com.backend.distributedqueue.models.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository for the {@link JobEntity}.
 *
 * This interface provides standard database operations (e.g., save, findById, delete)
 * for the JobEntity. The framework will provide the implementation at runtime.
 */
@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {
    // You can add custom query methods here if needed in the future, for example:
    // List<JobEntity> findByJobAction(String jobAction);
}