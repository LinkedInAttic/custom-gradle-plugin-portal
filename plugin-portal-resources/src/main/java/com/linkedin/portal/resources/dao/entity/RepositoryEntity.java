/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.resources.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "repository")
public class RepositoryEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String type;
    private String url;
    private String ivy;
    private String artifact;
    private boolean m2Compatible;

    public RepositoryEntity(String type, String url, String ivy, String artifact, boolean m2Compatible) {
        this.type = type;
        this.url = url;
        this.ivy = ivy;
        this.artifact = artifact;
        this.m2Compatible = m2Compatible;
    }

    public RepositoryEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getIvy() {
        return ivy;
    }

    public String getArtifact() {
        return artifact;
    }

    public boolean isM2Compatible() {
        return m2Compatible;
    }
}
