package com.linkedin.portal.resources.dao.repository;

import com.linkedin.portal.resources.dao.entity.PluginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PluginRepository extends JpaRepository<PluginEntity, Long> {

    PluginEntity findByPluginNameEquals(String name);
}
