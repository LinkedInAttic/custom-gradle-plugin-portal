/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
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
    private String docLink;

    @OneToMany
    @Cascade({CascadeType.ALL})
    private List<PluginVersionEntity> versions;

    public PluginEntity(String pluginName, String latestVersion, String docLink, List<PluginVersionEntity> versions) {
        this.pluginName = pluginName;
        this.latestVersion = latestVersion;
        this.versions = versions;
        this.docLink = docLink;
    }

    public PluginEntity() {
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getDocLink() {
        return docLink;
    }

    public List<PluginVersionEntity> getVersions() {
        return versions;
    }
}
