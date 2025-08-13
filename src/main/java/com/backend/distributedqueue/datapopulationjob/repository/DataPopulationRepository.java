package com.backend.distributedqueue.datapopulationjob.repository;

import com.backend.distributedqueue.datapopulationjob.models.DataPopulationPayloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DataPopulationRepository extends JpaRepository<DataPopulationPayloadEntity, UUID> {

}
