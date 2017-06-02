/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.resources.transform;

import com.linkedin.portal.model.IvyLayout;
import com.linkedin.portal.model.PluginIdContainer;
import com.linkedin.portal.model.PluginVersion;
import com.linkedin.portal.model.RepositoryDefinition;
import com.linkedin.portal.model.RepositoryType;
import com.linkedin.portal.resources.dao.entity.PluginEntity;
import com.linkedin.portal.resources.dao.entity.PluginVersionEntity;
import com.linkedin.portal.resources.dao.entity.RepositoryEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility to convert between database and rest models.
 */
public class Transformer {
    private Transformer() {
        //NOOP
    }

    /**
     * Convert a database model to a rest model
     * @param pluginEntity a database model
     * @return a rest model
     */
    public static PluginIdContainer fromPluginEntity(PluginEntity pluginEntity) {
        Map<String, PluginVersion> versionMap = pluginEntity.getVersions().stream()
                .map(Transformer::fromPluginVersionEntity).collect(Collectors.toMap(PluginVersion::getVersion, item -> item));

        return new PluginIdContainer(versionMap, pluginEntity.getPluginName(), pluginEntity.getLatestVersion(), pluginEntity.getDocLink());
    }

    /**
     * Convert database model to a rest model
     * @param entity a database model
     * @return a rest model
     */
    public static PluginVersion fromPluginVersionEntity(PluginVersionEntity entity) {

        Map<String, String> dependencyNotation = new HashMap<>();
        dependencyNotation.put("group", entity.getOrganization());
        dependencyNotation.put("name", entity.getName());
        dependencyNotation.put("version", entity.getVersion());

        if (null != entity.getClassifier() && !"".equals(entity.getClassifier())) {
            dependencyNotation.put("classifier", entity.getClassifier());
        }

        if (null != entity.getConfiguration() && !"".equals(entity.getConfiguration())) {
            dependencyNotation.put("configuration", entity.getConfiguration());
        }

        return new PluginVersion(entity.getPluginVersion(), dependencyNotation);
    }

    /**
     * Convert a database entries to a rest model.
     * @param pluginEntity Plugin Data Model
     * @param pluginVersion Plugin Version Model
     * @return a rest model
     */
    public static PluginVersionEntity fromPluginVersion(PluginEntity pluginEntity, PluginVersion pluginVersion) {
        Map<String, String> dependencyNotation = pluginVersion.getDependencyNotation();
        return new PluginVersionEntity(pluginEntity, pluginVersion.getVersion(), dependencyNotation.get("group"),
                dependencyNotation.get("name"), dependencyNotation.get("version"),
                dependencyNotation.get("configuration"), dependencyNotation.get("classifier"));
    }

    /**
     * Convert {@link RepositoryDefinition} to {@link RepositoryEntity}
     * @param definitions a rest model
     * @return a database model
     */
    public static RepositoryEntity fromRepositoryDefinitions(RepositoryDefinition definitions) {
        String type = definitions.getType().name();
        String url = definitions.getUrl();
        boolean m2compatable = false;
        String artifact = null;
        String ivy = null;

        if (definitions.getIvyLayout() != null) {
            m2compatable = definitions.getIvyLayout().isM2compatible();
            artifact = definitions.getIvyLayout().getArtifact();
            ivy = definitions.getIvyLayout().getIvy();
        }

        return new RepositoryEntity(type, url, ivy, artifact, m2compatable);
    }

    /**
     * Convert {@link RepositoryEntity} to {@link RepositoryDefinition}
     * @param repositoryEntity a database model
     * @return a rest model
     */
    public static RepositoryDefinition fromRepositoryEntity(RepositoryEntity repositoryEntity) {
        IvyLayout layout = null;
        if (repositoryEntity.getIvy() != null && repositoryEntity.getArtifact() != null) {
            layout = new IvyLayout(repositoryEntity.getIvy(), repositoryEntity.getArtifact(), repositoryEntity.isM2Compatible());
        }
        return new RepositoryDefinition(RepositoryType.valueOf(repositoryEntity.getType()), repositoryEntity.getUrl(), layout);
    }
}
