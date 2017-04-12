package com.linkedin.portal.resources.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "plugin_version")
public class PluginVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private PluginEntity pluginEntity;
    private String pluginVersion;
    private String organization;
    private String name;
    private String version;
    private String configuration;
    private String classifier;

    public PluginVersionEntity(PluginEntity pluginEntity, String pluginVersion, String organization,
                               String name, String version, String configuration, String classifier) {
        this.pluginEntity = pluginEntity;
        this.pluginVersion = pluginVersion;
        this.organization = organization;
        this.name = name;
        this.version = version;
        this.configuration = configuration;
        this.classifier = classifier;
    }

    public PluginVersionEntity() {
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public String getOrganization() {
        return organization;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getConfiguration() {
        return configuration;
    }

    public String getClassifier() {
        return classifier;
    }
}
