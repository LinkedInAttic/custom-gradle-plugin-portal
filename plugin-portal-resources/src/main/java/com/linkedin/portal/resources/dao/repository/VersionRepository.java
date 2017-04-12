package com.linkedin.portal.resources.dao.repository;

import com.linkedin.portal.resources.dao.entity.PluginEntity;
import com.linkedin.portal.resources.dao.entity.PluginVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionRepository extends JpaRepository<PluginVersionEntity, Long> {

    PluginVersionEntity findByPluginEntityAndPluginVersionEquals(PluginEntity plugin, String version);
}
