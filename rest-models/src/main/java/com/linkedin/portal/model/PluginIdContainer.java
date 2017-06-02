/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class PluginIdContainer {

    private static final String VERSIONS = "versions";
    private static final String PLUGIN_ID = "pluginId";
    private static final String DEFAULT_VERSION = "defaultVersion";
    private static final String DOCUMENTATION_LINK = "docLink";

    private final Map<String, PluginVersion> versionMap;
    private final String pluginId;
    private final String defaultVersion;
    private final String documentationLink;

    @JsonCreator
    public PluginIdContainer(@JsonProperty(value = VERSIONS, required = true) Map<String, PluginVersion> versionMap,
                             @JsonProperty(value = PLUGIN_ID, required = true) String pluginId,
                             @JsonProperty(value = DEFAULT_VERSION, required = true) String defaultVersion,
                             @JsonProperty(value = DOCUMENTATION_LINK, required = false) String documentationLink) {
        this.versionMap = versionMap;
        this.pluginId = pluginId;
        this.defaultVersion = defaultVersion;
        this.documentationLink = documentationLink;
    }

    @JsonProperty(VERSIONS)
    public Map<String, PluginVersion> getVersions() {
        return versionMap;
    }

    @JsonProperty(PLUGIN_ID)
    public String getPluginId() {
        return pluginId;
    }

    @JsonProperty(DEFAULT_VERSION)
    public String getDefaultVersion() {
        return defaultVersion;
    }

    @JsonProperty(DOCUMENTATION_LINK)
    public String getDocumentationLink() {
        return documentationLink;
    }
}
