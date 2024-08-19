package com.apiserver.apiserver.repository;

import com.apiserver.apiserver.entity.JobsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobsRepository extends JpaRepository<JobsEntity, Long> {
}