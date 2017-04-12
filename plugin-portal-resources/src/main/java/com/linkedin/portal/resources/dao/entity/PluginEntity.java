package com.linkedin.portal.resources.dao.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "plugin")
public class PluginEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String pluginName;
    private String latestVersion;

    @OneToMany
    @Cascade({CascadeType.ALL})
    private List<PluginVersionEntity> versions;

    public PluginEntity(String pluginName, String latestVersion, List<PluginVersionEntity> versions) {
        this.pluginName = pluginName;
        this.latestVersion = latestVersion;
        this.versions = versions;
    }

    public PluginEntity() {
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public List<PluginVersionEntity> getVersions() {
        return versions;
    }
}
