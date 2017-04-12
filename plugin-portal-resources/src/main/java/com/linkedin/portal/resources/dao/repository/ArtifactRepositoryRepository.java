package com.linkedin.portal.resources.dao.repository;

import com.linkedin.portal.resources.dao.entity.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtifactRepositoryRepository extends JpaRepository<RepositoryEntity, Long> {
}
